package account.mapper;

import account.dto.PayrollDTO;
import account.entity.Payroll;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PayrollMapper {

    public static Payroll toEntity(PayrollDTO payrollDTO){
        return Payroll.builder()
                .employee(payrollDTO.getEmployee())
                .period(payrollDTO.getPeriod())
                .salary(payrollDTO.getSalary())
                .build();
    }

    public static PayrollDTO toDTO(Payroll payroll){
        return PayrollDTO.builder()
                .name(payroll.getEmployee().getName())
                .lastname(payroll.getEmployee().getLastname())
                .period(payroll.getPeriod())
                .periodView(formatPeriod(payroll.getPeriod()))
                .salaryView(formatSalary(payroll.getSalary()))
                .build();
    }

    private static String formatSalary(Long salary) {
        return String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
    }

    private static String formatPeriod(YearMonth period) {
        return period.format(DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.US));
    }
}
