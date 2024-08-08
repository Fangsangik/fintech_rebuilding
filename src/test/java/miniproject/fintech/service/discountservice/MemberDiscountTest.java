package miniproject.fintech.service.discountservice;

import miniproject.fintech.domain.BankMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static miniproject.fintech.type.Grade.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberDiscountTest {

    @Autowired
    private DiscountService discountService;

    @Test
    void discount() {
        //given
        BankMember bankMember = new BankMember();
        bankMember.setGrade(VIP);
        bankMember.setAmount(2000);
        //when
        if (bankMember.getGrade().equals(VIP)) {
            discountService.discount(bankMember);
        }
        //then
        double expectedDiscount = bankMember.getAmount() - bankMember.getAmount() * 0.01;
        assertEquals(expectedDiscount, 1980);
    }
}