package account.acct;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.YearMonth;

@Data
public class PaymentDetails {

    @Id
    @GeneratedValue
    private long id;
    private String employee;
    private YearMonth period;
    private Long salary;
}
