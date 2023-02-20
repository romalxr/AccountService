package account.controller;

import account.dto.ChangeAccessDTO;
import account.dto.ChangeRoleDTO;
import account.dto.PasswordDTO;
import account.dto.UserDTO;
import account.service.EventService;
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
    EventService eventService;
    @Autowired
    UserService userService;

    @PostMapping("/auth/signup")
    public UserDTO register(@RequestBody UserDTO userDTO, @AuthenticationPrincipal UserDetails userDetails) {
        UserDTO user = userService.saveUser(userDTO);
        eventService.addEventCreateUser(userDetails, userDTO.getEmail().toLowerCase(), "/api/auth/signup");
        return user;
    }

    @PostMapping("/auth/changepass")
    public Object changePassword(@RequestBody PasswordDTO passwordDTO, @AuthenticationPrincipal UserDetails userDetails) {
        userService.updateUser(userDetails, passwordDTO);
        eventService.addEventChangePassword(userDetails.getUsername(), "/api/auth/changepass");
        return Map.of("email", userDetails.getUsername(),
                "status", "The password has been updated successfully");
    }

    @GetMapping("/admin/user")
    public List<UserDTO> getUserList() {
        return userService.findAll();
    }

    @PutMapping("/admin/user/access")
    public Object changeAccess(@RequestBody ChangeAccessDTO changeAccessDTO, @AuthenticationPrincipal UserDetails userDetails){
        userService.changeAccess(userDetails, changeAccessDTO);
        return Map.of("status", "User "+changeAccessDTO.getEmail().toLowerCase()+" "+changeAccessDTO.getOperation().toLowerCase()+"ed!");
    }

    @DeleteMapping ("/admin/user/{email}")
    public Object deleteUser(@PathVariable String email, @AuthenticationPrincipal UserDetails userDetails) {
        userService.delete(email);
        eventService.addEventDeleteUser(userDetails, email.toLowerCase(), "/api/admin/user");
        return Map.of("user", email, "status", "Deleted successfully!");
    }

    @PutMapping("/admin/user/role")
    public UserDTO changeRole(@RequestBody ChangeRoleDTO changeRole, @AuthenticationPrincipal UserDetails userDetails) {
        UserDTO user = userService.changeRole(changeRole);
        eventService.addEventChangeRole(userDetails, changeRole.getOperation().toUpperCase(),
                changeRole.getRole().toUpperCase(), user.getEmail().toLowerCase(), "/api/admin/user/role");
        return user;
    }
}
