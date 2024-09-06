package miniproject.fintech.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.DepositRepository;
import miniproject.fintech.repository.DiscountRepository;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.Grade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static miniproject.fintech.type.ErrorType.*;

@Service
@Slf4j
@Getter
public class DiscountService {

    private final DiscountRepository discountRepository;

    private static final double DISCOUNT_RATE = 0.01;
    private final DepositRepository depositRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository, DepositRepository depositRepository, MemberRepository memberRepository) {
        this.discountRepository = discountRepository;
        this.depositRepository = depositRepository;
        this.memberRepository = memberRepository;
    }

    public void applyDiscount(String userId, String password) {
        log.info("회원 ID {}에 대한 할인 적용 요청", userId);

        // 회원을 조회합니다.
        BankMember bankMember = memberRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: ID = {}", userId);
                    return new CustomError(MEMBER_NOT_FOUND);
                });

        // 비밀번호와 등급 확인
        if (!bankMember.getPassword().equals(password)) {
            log.error("비밀번호 불일치: 회원 ID = {}", userId);
            throw new CustomError(PASSWORD_INCORRECT);
        }

        if (bankMember.getGrade() == null || !bankMember.getGrade().equals(Grade.VIP)) {
            log.error("회원 등급 불일치: 회원 ID = {}, 등급 = {}", userId, bankMember.getGrade());
            throw new CustomError(GRADE_NOT_VIP);
        }

        // 할인 금액 계산 및 업데이트
        double originalAmount = bankMember.getAmount();
        double discountAmount = originalAmount * DISCOUNT_RATE;
        double newAmount = originalAmount - discountAmount;

        bankMember.setAmount((int) newAmount);
        discountRepository.save(bankMember);

        log.info("할인 적용 완료: 회원 ID = {}, 원래 금액 = {}, 할인 금액 = {}, 새로운 금액 = {}",
                userId, originalAmount, discountAmount, newAmount);
    }
}


