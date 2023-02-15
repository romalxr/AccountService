package account.repository;

import account.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String>{

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findFirstByEmailIgnoreCase(String email);
}