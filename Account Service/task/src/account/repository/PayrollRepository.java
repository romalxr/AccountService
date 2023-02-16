package account.repository;

import account.entity.Payroll;
import account.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends CrudRepository<Payroll, String> {
    List<Payroll> findAllByEmployee(User user);
    Optional<Payroll> findByEmployeeAndPeriod(User user, YearMonth period);
}
