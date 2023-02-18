package account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ChangeRoleDTO {
    @JsonProperty("user")
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String role;
    @NotBlank
    private String operation;
}
