package account.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String lastname;
    @Column(unique = true)
    private String email;
    private boolean accountLocked;
    private int failedAttempt;
    private Date lockTime;
    private String password;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    private Set<Role> userGroups;

    public void grantRole(Role role){
        if (userGroups == null){
            userGroups = new TreeSet<>();
        }
        userGroups.add(role);
    }
    public void removeRole(Role role){
        userGroups.remove(role);
    }
    public Set<Role> getUserGroups() {
        return userGroups;
    }
}