package account.controller;

import account.dto.UserDTO;
import account.entity.User;
import account.mapper.UserMapper;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    UserRepository userRepository;
    @GetMapping("/empl/payment")
    public UserDTO showPayroll(@AuthenticationPrincipal UserDetails userDetails){

        User user = userRepository.findFirstByEmailIgnoreCase(userDetails.getUsername()).get();
        return UserMapper.toDTO(user);
    }
}
