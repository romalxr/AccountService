package account.controller;

import account.dto.ChangeRoleDTO;
import account.dto.PasswordDTO;
import account.dto.UserDTO;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/auth/signup")
    public UserDTO register(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

    @PostMapping("/auth/changepass")
    public Object changePassword(@RequestBody PasswordDTO passwordDTO, @AuthenticationPrincipal UserDetails userDetails) {
        userService.updateUser(userDetails, passwordDTO);
        return Map.of("email", userDetails.getUsername(),
                "status", "The password has been updated successfully");
    }

    @GetMapping("/admin/user")
    public List<UserDTO> getUserList() {
        return userService.findAll();
    }

    @DeleteMapping ("/admin/user/{email}")
    public Object deleteUser(@PathVariable String email) {
        userService.delete(email);
        return Map.of("user", email,
                "status", "Deleted successfully!");
    }

    @PutMapping("/admin/user/role")
    public UserDTO changeRole(@RequestBody ChangeRoleDTO changeRole) {
        return userService.changeRole(changeRole);
    }
}
