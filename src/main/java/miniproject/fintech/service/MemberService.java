package miniproject.fintech.service;

import miniproject.fintech.domain.BankMember;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MemberService {
    BankMember save(BankMember bankMember);

    List<BankMember> findAll();

    Optional<BankMember> findById(Long id);

    Optional<BankMember> create(Long id, String newAccount);

    void delete(Long id, String password);

}
