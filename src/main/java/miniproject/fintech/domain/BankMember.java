package miniproject.fintech.domain;

import jakarta.persistence.*;
import lombok.*;
import miniproject.fintech.type.Grade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter //테스트 위함
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String accountNumber;
    private int amount;

    private String password;
    private int age;
    private LocalDate birth;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    private String address;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankMember that = (BankMember) o;
        return amount == that.amount && age == that.age && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(accountNumber, that.accountNumber) && Objects.equals(password, that.password) && Objects.equals(birth, that.birth) && Objects.equals(createdAt, that.createdAt) && Objects.equals(deletedAt, that.deletedAt) && grade == that.grade && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, accountNumber, amount, password, age, birth, createdAt, deletedAt, grade, address);
    }
}
