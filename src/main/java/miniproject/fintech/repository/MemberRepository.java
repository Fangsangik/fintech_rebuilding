package miniproject.fintech.repository;

import miniproject.fintech.domain.BankMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

//JpaRepository는 두개의 제네릭 타입 필요
//첫번째는 엔티티 ID 타입, 두번째는 엔티티 타입
@Repository
public interface MemberRepository {

     BankMember save(BankMember bankMember);

    List<BankMember> findAll();

    Optional<BankMember> findById(Long id);

    Optional<BankMember> createdBy(Long id, String newAccount);

    void deletedById(Long id);
}
