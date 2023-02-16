package account.service;

import account.dto.PasswordDTO;
import account.dto.UserDTO;
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

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    public UserDTO saveUser(@Valid UserDTO userDTO) {
        checkUserExist(userDTO.getEmail());
        checkBadPassword(userDTO.getPassword());
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
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

    void checkUserNotExist(String email) {
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not exist!");
        }
    }
    User getUserByEmail(String email) {
        checkUserNotExist(email);
        return userRepository.findFirstByEmailIgnoreCase(email).get();
    }

}
