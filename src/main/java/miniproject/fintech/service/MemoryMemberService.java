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

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryMemberService implements MemberService{

    @Autowired
    private final MemberRepository memberRepository;

    @Override
    public BankMember save(BankMember bankMember){
        return memberRepository.save(bankMember);
    }

    @Override
    // ID로 BankMember 찾기 메서드
    public Optional<BankMember> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    // 모든 BankMember 찾기 메서드
    public List<BankMember> findAll() {
        return new ArrayList<>(memberRepository.findAll());
    }

    @Override
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

    @Override
    //회원 탈퇴
    public void delete(Long id, String password) {
        BankMember bankMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));

        deleteValidation(id, password, bankMember);
    }

    private void deleteValidation(Long id, String password, BankMember bankMember) {
        if (bankMember.getPassword().equals(password)){
            memberRepository.deletedById(id);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
