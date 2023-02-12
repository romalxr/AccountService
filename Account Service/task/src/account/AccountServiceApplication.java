package account;

import account.acct.EmployeeDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SpringBootApplication
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    public static void main2(String[] args) {

        YearMonth period = YearMonth.of(2011, 11);
        System.out.println(period.format(DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.US)));

    }
}