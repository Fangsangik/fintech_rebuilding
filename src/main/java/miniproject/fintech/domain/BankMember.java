package miniproject.fintech.domain;

import lombok.*;
import miniproject.fintech.type.Grade;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter //테스트 위함
@Entity
@Builder (toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BankMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String accountNumber;
    private int amount;
    private String email;
    private String password;
    private int age;
    private LocalDate birth;
    private long curAmount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    private String address;

    @OneToMany(mappedBy = "bankMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>(); // 빈 리스트로 초기화

    @OneToMany(mappedBy = "bankMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankMember that = (BankMember) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && grade == that.grade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, grade);
    }

    @Override
    public String toString() {
        return "BankMember{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", birth=" + birth +
                ", curAmount=" + curAmount +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                ", grade=" + grade +
                ", address='" + address + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
