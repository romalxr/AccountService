package account.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewPassDetails {
    @NotBlank(message = "Password length must be 12 chars minimum!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    String new_password;
}
