package account.acct;

import account.auth.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    boolean existsByEmployeeIgnoreCaseAndPeriod(String employee, YearMonth period);

    Optional<Payment> findByEmployeeIgnoreCaseAndPeriod(String employee, YearMonth period);

    List<Payment> findAllByEmployeeIgnoreCaseOrderByPeriodDesc(String employee);
}
