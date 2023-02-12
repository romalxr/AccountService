package account.acct;

import lombok.*;

import javax.persistence.*;
import java.time.YearMonth;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue
    private long id;
    private String employee;
    private YearMonth period;
    private Long salary;
}
