package miniproject.fintech.repository.memberrepository;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.BankMember;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final JpaMemberRepository jpaMemberRepository;

    @Override
    public BankMember save(BankMember bankMember) {
        return jpaMemberRepository.save(bankMember);
    }

    @Override
    public List<BankMember> findAll() {
        return jpaMemberRepository.findAll();
    }

    @Override
    public Optional<BankMember> findById(Long id) {
        return jpaMemberRepository.findById(id);
    }

    @Override
    public Optional<BankMember> createdBy(Long id, String newAccount) {
        BankMember bankMember = new BankMember();
        bankMember.setId(id);
        bankMember.setAccountNumber(newAccount);
        return Optional.of(jpaMemberRepository.save(bankMember));
    }

    @Override
    public void deletedById(Long id) {
        BankMember bankMember = jpaMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

       if (bankMember.getId().equals(id)){
           jpaMemberRepository.delete(bankMember);
       } else {
           throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
       }
    }
}
