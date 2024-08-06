package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private final MemberRepository memberRepository;

    public BankMember save(BankMember bankMember){
        return memberRepository.save(bankMember);
    }

    // ID로 BankMember 찾기 메서드
    public BankMember findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 값입니다."));
    }

    // 모든 BankMember 찾기 메서드
    public List<BankMember> findAll() {
        return new ArrayList<>(memberRepository.findAll());
    }

    //신규회원 생성
    public Optional<BankMember> create(Long id, String newAccount) {
        Optional<BankMember> existingBankMember = memberRepository.findById(id); //기존회원

        BankMember newBankMember = validationCreateNewMember(id, newAccount, existingBankMember);

        return Optional.of(memberRepository.save(newBankMember));
    }

    private static BankMember validationCreateNewMember(Long id, String newAccount, Optional<BankMember> existingBankMember) {
        if (existingBankMember.isPresent()){
            throw new IllegalArgumentException("중복된 회원입니다.");
        }

        BankMember newBankMember = new BankMember();// 신규회원
        newBankMember.setId(id);
        newBankMember.setAccountNumber(newAccount);
        return newBankMember;
    }

    //회원 탈퇴
    public void delete(Long id, String password) {
        BankMember bankMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));

        validationOfDelete(id, password, bankMember);
        memberRepository.delete(bankMember);
    }

    private static void validationOfDelete(Long id, String password, BankMember bankMember) {
//        if (bankMember.getId().equals(id) && bankMember.getPassword().equals(password)){
//            log.info("회원 탈퇴를 진행하겠습니다.", id);
//        } else {
//            throw new IllegalArgumentException("잘못된 접근 입니다.");
//        }
        if (!bankMember.getPassword().equals(password)){
            throw new IllegalArgumentException("잘못된 접근 입니다.");
        }
        log.info("회원 탈퇴를 진행하겠습니다.", id);
    }
}
