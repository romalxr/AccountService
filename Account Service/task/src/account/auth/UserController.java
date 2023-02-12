package account.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;

    private boolean badPassword(String password) {
        List<String> badPasswords = new ArrayList<>();
        badPasswords.add("PasswordForJanuary");
        badPasswords.add("PasswordForFebruary");
        badPasswords.add("PasswordForMarch");
        badPasswords.add("PasswordForApril");
        badPasswords.add("PasswordForMay");
        badPasswords.add("PasswordForJune");
        badPasswords.add("PasswordForJuly");
        badPasswords.add("PasswordForAugust");
        badPasswords.add("PasswordForSeptember");
        badPasswords.add("PasswordForOctober");
        badPasswords.add("PasswordForNovember");
        badPasswords.add("PasswordForDecember");

        return badPasswords.stream().anyMatch(s -> s.equals(password));
    }

    @PostMapping("/signup")
    public User register(@Valid @RequestBody User user) {
        if (userRepo.existsByEmailIgnoreCase(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }

        if (badPassword(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        return user;
    }

    @PostMapping("/changepass")
    public Object changePassword(@AuthenticationPrincipal UserDetails details, @RequestBody String newDetails2) {

        JSONObject obj = new JSONObject(newDetails2);
        String password = obj.getString("new_password");
        if (password == null || password.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");
        }

        NewPassDetails newDetails = new NewPassDetails();
        newDetails.setNew_password(password);

        User user = userRepo.findFirstByEmailIgnoreCase(details.getUsername()).get();
        if (encoder.matches(newDetails.getNew_password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
        if (badPassword(newDetails.getNew_password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
        user.setPassword(encoder.encode(newDetails.getNew_password()));
        userRepo.save(user);
        return Map.of("email", details.getUsername().toLowerCase(), "status", "The password has been updated successfully");
    }
}
