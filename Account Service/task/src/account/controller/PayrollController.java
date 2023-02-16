package account.controller;

import account.dto.PayrollDTO;
import account.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PayrollController {

    @Autowired
    PayrollService payrollService;

    @GetMapping("/empl/payment")
    public Object showPayroll(@RequestParam(required = false) String period, @AuthenticationPrincipal UserDetails userDetails){
        if (period == null){
            return payrollService.getPayrollsByEmployee(userDetails);
        }
        return payrollService.getPayrollByEmployeeAndPeriod(period, userDetails);
    }

    @PutMapping("/acct/payments")
    public Map<String, String> changeSalary(@RequestBody PayrollDTO payroll){
        payrollService.updatePayment(payroll);
        return Map.of("status", "Updated successfully!");
    }

    @PostMapping("/acct/payments")
    public Object uploadPayrolls(@RequestBody List<PayrollDTO> payrolls){
        payrollService.uploadPayrolls(payrolls);
        return Map.of("status", "Added successfully!");
    }
}
