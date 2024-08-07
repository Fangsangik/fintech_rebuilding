package miniproject.fintech.repository.memberrepository;

import miniproject.fintech.domain.BankMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<BankMember, Long> {
}
