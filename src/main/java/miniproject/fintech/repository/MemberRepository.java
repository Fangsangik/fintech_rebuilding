package miniproject.fintech.repository;

import miniproject.fintech.domain.BankMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//JpaRepository는 두개의 제네릭 타입 필요
//첫번째는 엔티티 ID 타입, 두번째는 엔티티 타입
@Repository
public interface MemberRepository extends JpaRepository<BankMember, Long> {
}
