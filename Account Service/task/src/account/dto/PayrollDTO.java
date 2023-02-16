package account.dto;

import account.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.YearMonth;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDTO {
    @JsonIgnore
    User employee;
    @NotBlank
    @JsonProperty(value = "employee", access = JsonProperty.Access.WRITE_ONLY)
    String email;
    @JsonFormat(pattern="MM-y")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    YearMonth period;
    @Min(value = 0, message = "Salary cannot be less than 0!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long salary;
    String name;
    String lastname;
    @JsonProperty(value = "period", access = JsonProperty.Access.READ_ONLY)
    String periodView;
    @JsonProperty(value = "salary", access = JsonProperty.Access.READ_ONLY)
    String salaryView;
}
