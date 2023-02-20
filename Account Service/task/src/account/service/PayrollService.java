package account.service;

import account.dto.PayrollDTO;
import account.entity.Payroll;
import account.entity.User;
import account.mapper.PayrollMapper;
import account.repository.PayrollRepository;
import account.repository.UserRepository;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class PayrollService {

    @Autowired
    PayrollRepository payrollRepository;
    @Autowired
    @Lazy
    UserService userService;
    @Autowired
    UserRepository userRepository;

    public void uploadPayrolls(@Valid List<PayrollDTO> payrollDTOs) {
        payrollDTOs.forEach(p -> p.setEmployee(userService.getUserByEmail(p.getEmail())));
        List<Payroll> payrolls = payrollDTOs.stream().map(PayrollMapper::toEntity).toList();
        try {
            payrollRepository.saveAll(payrolls);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payroll not unique!");
        }
    }

    public void updatePayment(@Valid PayrollDTO payrollDTO) {
        User user = userService.getUserByEmail(payrollDTO.getEmail());
        Optional<Payroll> payrollOpt = payrollRepository.findByEmployeeAndPeriod(user, payrollDTO.getPeriod());
        if (payrollOpt.isPresent()) {
            Payroll payroll = payrollOpt.get();
            payroll.setSalary(payrollDTO.getSalary());
            payrollRepository.save(payroll);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payroll not found!");
        }
    }

    public List<PayrollDTO> getPayrollsByEmployee(UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<Payroll> payrollById = payrollRepository.findAllByEmployee(user);
        return payrollById.stream()
                .map(PayrollMapper::toDTO)
                .sorted(Comparator.comparing(PayrollDTO::getPeriod).reversed())
                .toList();
    }

    public PayrollDTO getPayrollByEmployeeAndPeriod(String period, UserDetails userDetails) {
        YearMonth periodYM = parsePeriod(period);
        User user = userService.getUserByEmail(userDetails.getUsername());
        Optional<Payroll> payrollByPeriod = payrollRepository.findByEmployeeAndPeriod(user, periodYM);
        return payrollByPeriod.map(PayrollMapper::toDTO).orElse(null);
    }

    private static YearMonth parsePeriod(String period) {
        YearMonth periodYM;
        try {
            periodYM = YearMonth.of(Integer.parseInt(period.substring(3)), Integer.parseInt(period.substring(0, 2)));
        } catch (DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong month in params");
        }
        return periodYM;
    }

}
