package account.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Entity
@Data
public class User {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Lastname is required")
    private String lastname;
    @NotBlank(message = "Email is required")
    @Email
    @Pattern(regexp = ".+@acme.com", message = "Domain should be @acme.com")
    private String email;
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;
}