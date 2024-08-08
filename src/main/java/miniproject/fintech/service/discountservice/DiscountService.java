package miniproject.fintech.service.discountservice;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.discountrepository.DiscountRepository;
import miniproject.fintech.type.Grade;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@Getter
public class DiscountService {

    private final DiscountRepository discountRepository;

    private static final double DISCOUNT_RATE = 0.01;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public void discount(BankMember bankMember) {
        if (bankMember.getGrade() != null && bankMember.getGrade().equals(Grade.VIP)){
            double discountAmount = bankMember.getAmount() - (bankMember.getAmount() * DISCOUNT_RATE);
            log.info("할인 금액 {} :", discountAmount);
        }
    }
}
