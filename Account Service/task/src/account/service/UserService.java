package account.service;

import account.dto.ChangeRoleDTO;
import account.dto.PasswordDTO;
import account.dto.UserDTO;
import account.entity.Role;
import account.entity.User;
import account.mapper.UserMapper;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
public class UserService {

    public enum Operation {GRANT,REMOVE}

    @Autowired
    UserRepository userRepository;
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

    User getUserByEmail(String email) {
        return getUserByEmail(email, false);
    }

    User getUserByEmail(String email, boolean stupidTest) {
        checkUserNotExist(email, stupidTest);
        return userRepository.findFirstByEmailIgnoreCase(email).get();
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