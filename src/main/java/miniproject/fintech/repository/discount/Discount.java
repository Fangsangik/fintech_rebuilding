package miniproject.fintech.repository.discount;

import miniproject.fintech.domain.BankMember;
import org.springframework.stereotype.Repository;

@Repository
public interface Discount {
    void discount(BankMember bankMember, int fee);
}
