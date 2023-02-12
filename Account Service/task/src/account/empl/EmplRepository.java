package account.empl;

import account.auth.User;
import account.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;



public class EmplRepository {


    UserRepository userRepo;


    public User payment(@AuthenticationPrincipal UserDetails details) {
        Optional<User> user = userRepo.findFirstByEmailIgnoreCase(details.getUsername());
        return user.get();
    }
}
