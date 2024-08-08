package miniproject.fintech.repository;

import miniproject.fintech.domain.BankMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<BankMember, Long> {
}
