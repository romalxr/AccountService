package account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordDTO {

    @NotBlank
    @Length(min = 12, message = "Password length must be 12 chars minimum!")
    @JsonProperty("new_password")
    String newPassword;
}