package account.acct;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeDataDto {
    private String name;
    private String lastname;
    //@JsonFormat(pattern = "MMMM-yyyy")
    private String period;
    private String salary;

}
