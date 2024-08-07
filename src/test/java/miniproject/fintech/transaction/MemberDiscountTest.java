package miniproject.fintech.transaction;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.service.discount.MemberDiscountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static miniproject.fintech.type.Grade.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberDiscountTest {

    @Autowired
    private MemberDiscountService memberDiscount;

    @Test
    void discount() {
        //given
        BankMember bankMember = new BankMember();
        bankMember.setGrade(VIP);
        bankMember.setAmount(2000);
        //when
        if (bankMember.getGrade().equals(VIP)) {
            memberDiscount.discount(bankMember, (int) bankMember.getAmount());
        }
        //then
        double expectedDiscount = 2000 * 0.01;
        assertEquals(expectedDiscount, memberDiscount.getTotal());
    }
}