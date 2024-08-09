package miniproject.fintech.service.memberservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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
    @Transactional(readOnly = true)
    // ID로 BankMember 찾기 메서드
    public Optional<BankMember> findById(BankMember bankMember) {
        return memberRepository.findById(bankMember.getId());
    }

    @Override
    // 모든 BankMember 찾기 메서드
    public List<BankMember> findAll() {
        return new ArrayList<>(memberRepository.findAll());
    }

    @Override
    @Transactional
    //신규회원 생성
    public BankMember createBankMember(BankMemberDto bankMemberDto, BankMember bankMember) {
        validationCreateNewMember(bankMemberDto);

        BankMember newBankMember = BankMember.builder()
                .name(bankMemberDto.getName())
                .email(bankMemberDto.getEmail())
                .password(bankMemberDto.getPassword())
                .address(bankMemberDto.getAddress())
                .createdAt(bankMemberDto.getCreatedAt())
                .accountNumber(bankMemberDto.getAccountNumber())
                .build();


        return memberRepository.save(newBankMember);
    }



    private void validationCreateNewMember(BankMemberDto bankMemberDto) {
        // 이메일 또는 계좌 번호로 중복 검사 수행
        Optional<BankMember> existingBankMember =
                Optional.ofNullable(memberRepository.findByEmail(bankMemberDto.getEmail()));
        if (existingBankMember.isPresent()) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        Optional<BankMember> existingBankMemberByAccountNumber = memberRepository.findByAccountNumber(bankMemberDto.getAccountNumber());
        if (existingBankMemberByAccountNumber.isPresent()) {
            throw new IllegalArgumentException("중복된 계좌 번호입니다.");
        }
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
    public BankMember updateMember(BankMember bankMember, BankMemberDto updatedMemberDto) {
        BankMember existingMember = validationOfId(bankMember.getId(), bankMember);

        BankMember updatedBankmember = existingMember.toBuilder()
                .age(updatedMemberDto.getAge())
                .name(updatedMemberDto.getName())
                .address(updatedMemberDto.getAddress())
                .email(updatedMemberDto.getEmail())
                .build();

        return memberRepository.save(updatedBankmember);
    }

    private BankMember validationOfId(Long bankMemberId, BankMember bankMember) {
        if (bankMemberId == null) {
            throw new IllegalArgumentException("잘못된 값 입니다.");
        }

        BankMember existingBankMember = memberRepository.findById(bankMemberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));

        if (!existingBankMember.getId().equals(bankMember.getId())) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        return existingBankMember;
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
