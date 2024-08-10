package miniproject.fintech.service.memberservice;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
public interface MemberService {
    BankMember save(BankMember bankMember);

    List<BankMember> findAll();

    Optional<BankMember> findById(Long id);

    BankMember createBankMember(BankMemberDto bankMemberDto);

    void deleteById(Long id, String password);

    BankMember getBankMemberById(Long bankMemberId);

    List<Account> findAccountByMemberId(Long id);

    BankMember updateMember(BankMember bankMember, BankMemberDto updatedMemberDto);

    Page<BankMember> findAll(Pageable pageable);
}
