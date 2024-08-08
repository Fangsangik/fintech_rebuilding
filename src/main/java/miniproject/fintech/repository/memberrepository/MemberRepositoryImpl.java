package miniproject.fintech.repository.memberrepository;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.BankMember;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class MemberRepositoryImpl{

    private final @Lazy MemberRepository memberRepository;

    public BankMember save(BankMember bankMember) {
        return memberRepository.save(bankMember);
    }


    public List<BankMember> findAll() {
        return memberRepository.findAll();
    }


    public Optional<BankMember> findById(Long id) {
        return memberRepository.findById(id);
    }


    public Optional<BankMember> createdBy(Long id, String newAccount) {
        BankMember bankMember = new BankMember();
        bankMember.setId(id);
        bankMember.setAccountNumber(newAccount);
        return Optional.of(memberRepository.save(bankMember));
    }


    public void deletedById(Long id) {
        BankMember bankMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

       if (bankMember.getId().equals(id)){
           memberRepository.delete(bankMember);
       } else {
           throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
       }
    }
}
