package miniproject.fintech.service.discount;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.type.Grade;
import org.springframework.stereotype.Service;

@Service
public class MemberDiscountService implements Discount {

    private static final double DISCOUNT_RATE = 0.01;
    double total = 0;

    @Override
    public void discount(BankMember bankMember, int fee) {
        if (bankMember.getGrade() != null && bankMember.getGrade().equals(Grade.VIP)){
            total = fee * DISCOUNT_RATE;
        }
    }

    public double getTotal(){
        return total;
    }
}
