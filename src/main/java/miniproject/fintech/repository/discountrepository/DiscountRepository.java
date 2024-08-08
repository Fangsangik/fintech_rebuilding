package miniproject.fintech.repository.discountrepository;

import miniproject.fintech.domain.BankMember;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository {
    void discount(BankMember bankMember);
}
