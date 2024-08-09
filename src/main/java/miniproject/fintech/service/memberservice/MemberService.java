package miniproject.fintech.service.memberservice;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
public interface MemberService {
    BankMember save(BankMember bankMember);

    List<BankMember> findAll();

    Optional<BankMember> findById(BankMember bankMember);

    BankMember createBankMember(BankMemberDto bankMemberDto, BankMember bankMember);

    void deleteById(Long id, String password);

    BankMember updateMember(BankMember bankMember, BankMemberDto updatedMemberDto);

}
