package account.acct;

import account.auth.User;
import account.auth.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class AcctController {
    private PaymentService paymentService;
    @Autowired
    UserRepository userRepo;

    @PostMapping("api/acct/payments")
    //public StatusDto addPayments(@RequestBody @UniqueElements List<@Valid PaymentDto> payments) {
    public StatusDto addPayments(@RequestBody List<@Valid PaymentDto> payments) {
            return paymentService.addPayments(payments);
    }

    @PutMapping("api/acct/payments")
    public StatusDto updatePayments(@RequestBody @Valid PaymentDto paymentDto) {
        return paymentService.updatePayment(paymentDto);
    }

    @GetMapping("api/empl/payment")
    public Object getPayment(@AuthenticationPrincipal UserDetails details,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "MM-yyyy") Calendar period) {
        User user = userRepo.findFirstByEmailIgnoreCase(details.getUsername()).get();
        if (period != null) {
            return paymentService.getCurrentEmployeeDataByPeriod(user, calendarToYearMonth(period));
        } else {
            return paymentService.getAllCurrentEmployeeData(user);
        }
    }

    private YearMonth calendarToYearMonth(Calendar calendar) {
        return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }
}