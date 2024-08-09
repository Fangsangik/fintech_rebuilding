package miniproject.fintech.repository;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//JpaRepository는 두개의 제네릭 타입 필요
//첫번째는 엔티티 ID 타입, 두번째는 엔티티 타입
@Repository
public interface MemberRepository extends JpaRepository<BankMember, Long>{

    Optional<BankMember> findByAccountNumber(String accountNumber);

    Page<BankMember> findAll(Pageable pageable);

    BankMember findByEmail(String email);

}
