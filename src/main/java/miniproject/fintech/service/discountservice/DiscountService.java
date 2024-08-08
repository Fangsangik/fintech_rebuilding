package miniproject.fintech.service.discountservice;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.DiscountRepository;
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

    public void applyDiscount(Long memberId, String password) {
        BankMember bankMember = discountRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (bankMember.getPassword().equals(password) && bankMember.getGrade() != null && bankMember.getGrade().equals(Grade.VIP)) {
            double discountAmount = bankMember.getAmount() - (bankMember.getAmount() * DISCOUNT_RATE);
            log.info("할인 금액: {}", discountAmount);
            // 할인 금액 적용 후 업데이트
            bankMember.setAmount((int) discountAmount);
            discountRepository.save(bankMember);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않거나 회원 등급이 VIP가 아닙니다.");
        }
    }
}



