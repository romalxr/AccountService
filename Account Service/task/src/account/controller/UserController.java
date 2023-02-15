package account.controller;

import account.dto.PasswordDTO;
import account.dto.UserDTO;
import account.entity.User;
import account.mapper.UserMapper;
import account.repository.UserRepository;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    UserService userService;
    @PostMapping("/signup")
    public UserDTO register(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

    @PostMapping("/changepass")
    public Object changePassword(@RequestBody PasswordDTO passwordDTO, @AuthenticationPrincipal UserDetails userDetails) {
        userService.updateUser(userDetails, passwordDTO);
        return Map.of("email", userDetails.getUsername(),
                "status", "The password has been updated successfully");
    }

}
