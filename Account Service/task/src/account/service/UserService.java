package account.service;

import account.dto.ChangeAccessDTO;
import account.dto.ChangeRoleDTO;
import account.dto.PasswordDTO;
import account.dto.UserDTO;
import account.entity.Operation;
import account.entity.Role;
import account.entity.User;
import account.mapper.UserMapper;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Service
@Validated
public class UserService {
    public static final int MAX_FAILED_ATTEMPTS = 4;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Lazy
    private EventService eventService;

    @Autowired
    private PasswordEncoder encoder;

    public UserDTO saveUser(@Valid UserDTO userDTO) {
        checkUserExist(userDTO.getEmail());
        checkBadPassword(userDTO.getPassword());
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        user.grantRole(newUserRole());
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    public void updateUser(UserDetails userDetails, @Valid PasswordDTO passwordDTO) {
        checkBadPassword(passwordDTO.getNewPassword());
        checkNewPassword(userDetails.getUsername(), passwordDTO.getNewPassword());
        User user = getUserByEmail(userDetails.getUsername());
        user.setPassword(encoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);
    }

    public List<UserDTO> findAll() {
        List<User> userList = (List<User>) userRepository.findAll();
        return userList.stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public void delete(String email) {
        User user = getUserByEmail(email, true);
        checkRemovingAdmin(user, Role.ADMINISTRATOR);
        userRepository.delete(user);
    }

    public UserDTO changeRole(@Valid ChangeRoleDTO changeRole) {
        User user = getUserByEmail(changeRole.getEmail(), true);
        Role role = parseRole(changeRole.getRole());
        Operation operation = parseOperation(changeRole.getOperation());

        if (operation == Operation.GRANT) {
            checkUserHasRole(user, role);
            checkConflictRole(user, role);
            user.grantRole(role);
        } else if (operation == Operation.REMOVE) {
            checkUserHasNotRole(user, role);
            checkRemovingAdmin(user, role);
            checkRemovingLastRole(user);
            user.removeRole(role);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected operation!");
        }

        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    public User getUserByEmail(String email) {
        return getUserByEmail(email, false);
    }

    User getUserByEmail(String email, boolean stupidTest) {
        checkUserNotExist(email, stupidTest);
        return userRepository.findFirstByEmailIgnoreCase(email).get();
    }

    public void registerSuccessLogin(String email) {
        User user = getUserByEmail(email);
        resetFailedAttempts(user);
    }

    public void registerBadLogin(String email, String path) {
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        User user = getUserByEmail(email);
        if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS) {
            increaseFailedAttempts(user);
        } else {
            eventService.addEventBruteForce(email.toLowerCase(), path);
            lock(email, user, path);
        }
    }

    public void resetFailedAttempts(User user) {
        if (user.getFailedAttempt() > 0) {
            user.setFailedAttempt(0);
            userRepository.save(user);
        }
    }

    public void increaseFailedAttempts(User user) {
        user.setFailedAttempt(user.getFailedAttempt() + 1);
        userRepository.save(user);
    }

    public void changeAccess(UserDetails userDetails, ChangeAccessDTO changeAccessDTO) {
        User user = getUserByEmail(changeAccessDTO.getEmail());
        checkLockingAdmin(user);
        if ("LOCK".equalsIgnoreCase(changeAccessDTO.getOperation())){
            lock(userDetails.getUsername(), user, "/api/admin/user/access");
        } else if ("UNLOCK".equalsIgnoreCase(changeAccessDTO.getOperation())) {
            unlock(userDetails.getUsername(), user, "/api/admin/user/access");
        }
    }

    public void unlock(String subjectEmail, User user, String path) {
        user.setAccountLocked(false);
        user.setFailedAttempt(0);
        user.setLockTime(null);
        userRepository.save(user);
        eventService.addEventUnlockUser(subjectEmail.toLowerCase(), user.getEmail().toLowerCase(), path);
    }

    public void lock(String subjectEmail, User user, String path) {
        if (userHasRole(user, Role.ADMINISTRATOR)) {
            return;
        }
        user.setAccountLocked(true);
        user.setLockTime(new Date());
        userRepository.save(user);
        eventService.addEventLockUser(subjectEmail.toLowerCase(), user.getEmail().toLowerCase(), path);
    }

    public void unlockWhenTimeExpired(String email, String path) {
        User user = getUserByEmail(email);
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            unlock(user.getEmail(), user, path);
        }
    }

    private void checkConflictRole(User user, Role role) {
        if (role == Role.ADMINISTRATOR ||
                user.getUserGroups().contains(Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user cannot combine administrative and business roles!");
        }
    }

    private void checkRemovingLastRole(User user) {
        if (user.getUserGroups().size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }
    }

    private void checkLockingAdmin(User user) {
        if (userHasRole(user, Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }
    }

    private void checkRemovingAdmin(User user, Role role) {
        if (role == Role.ADMINISTRATOR && userHasRole(user, role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
    }

    private void checkUserHasRole(User user, Role role) {
        if (userHasRole(user, role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user already have a role!");
        }
    }

    private void checkUserHasNotRole(User user, Role role) {
        if (!userHasRole(user, role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }
    }

    private Operation parseOperation(String operation) {
        try {
            return Operation.valueOf(operation.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Operation not found!");
        }
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }
    }

    private boolean userHasRole(User user, Role role) {
        return user.getUserGroups().contains(role);
    }

    private Role newUserRole() {
        if (findAll().isEmpty()) {
            return Role.ADMINISTRATOR;
        } else {
            return Role.USER;
        }
    }

    private void checkNewPassword(String email, String newPassword) {
        User user = getUserByEmail(email);
        if (encoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
    }

    private void checkBadPassword(String password) {
        List<String> badPasswords = List.of(
                "PasswordForJanuary",
                "PasswordForFebruary",
                "PasswordForMarch",
                "PasswordForApril",
                "PasswordForMay",
                "PasswordForJune",
                "PasswordForJuly",
                "PasswordForAugust",
                "PasswordForSeptember",
                "PasswordForOctober",
                "PasswordForNovember",
                "PasswordForDecember"
        );

        if (badPasswords.contains(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
    }

    void checkUserExist(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
    }

    void checkUserNotExist(String email, boolean stupidTest) {
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            if (stupidTest) {
                //in case DELETE user test want different status what is stupid
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found!");
            }
        }
    }

}