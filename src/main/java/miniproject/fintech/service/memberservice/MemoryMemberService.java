package miniproject.fintech.service.memberservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryMemberService implements MemberService{

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
    @Transactional
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
    @Transactional
    //회원 탈퇴
    public void deleteById(Long id, String password) {
        BankMember bankMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));

        deleteValidation(id, password, bankMember);
    }

    private void deleteValidation(Long id, String password, BankMember bankMember) {
        if (bankMember.getPassword().equals(password)){
            memberRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public BankMember updateMember(Long id ,BankMember updatedMember) {
        BankMember existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));


        /**
         리펙토링 할때 손 보자... 코드 중복이 너무 많다.
         */
        if (updatedMember.getName() != null) {
            existingMember.setName(updatedMember.getName());
        }

        if (updatedMember.getPassword() != null) {
            existingMember.setPassword(updatedMember.getPassword());
        }

        if (updatedMember.getEmail() != null) {
            existingMember.setEmail(updatedMember.getEmail());
        }

        return memberRepository.save(existingMember);
    }

    public List<Account> findAccountByMemberId (Long id) {
        BankMember member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return new ArrayList<>(member.getAccounts());
    }



    public Page<BankMember> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
}
