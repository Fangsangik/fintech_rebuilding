package miniproject.fintech.repository;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//JpaRepository는 두개의 제네릭 타입 필요
//첫번째는 엔티티 ID 타입, 두번째는 엔티티 타입
@Repository
public interface MemberRepository extends JpaRepository<BankMember, Long>{

    Optional<BankMember> findByAccountNumber(String accountNumber);

    Page<BankMember> findAll(Pageable pageable);

    Optional<BankMember> findByEmail(String email);

    @Query("SELECT m FROM BankMember m " +
            "LEFT JOIN FETCH m.roles " +
            "LEFT JOIN FETCH m.accounts " +
            "WHERE m.id = :id")
    Optional<BankMember> findByIdWithRolesAndAccounts(@Param("id") Long id);
}
