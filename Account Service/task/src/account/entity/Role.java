package account.entity;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMINISTRATOR,
    USER,
    AUDITOR,
    ACCOUNTANT;

    @Override
    public String getAuthority() {
        return name();
    }
}
