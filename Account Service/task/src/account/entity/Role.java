package account.entity;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public enum Role implements GrantedAuthority {
    ADMINISTRATOR,
    USER,
    ACCOUNTANT;

    @Override
    public String getAuthority() {
        return name();
    }
}
