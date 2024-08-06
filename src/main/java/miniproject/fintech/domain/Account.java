package miniproject.fintech.domain;

import jakarta.persistence.*;
import lombok.*;
import miniproject.fintech.type.AccountStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long accountNumber;
    private String bankName;
    private String password;
    private long leftAmount;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNumber == account.accountNumber && leftAmount == account.leftAmount
                && Objects.equals(bankName, account.bankName) && Objects.equals(password, account.password) &&
                accountStatus == account.accountStatus && Objects.equals(createdAt, account.createdAt)
                && Objects.equals(deletedAt, account.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, bankName, password, leftAmount, accountStatus, createdAt, deletedAt);
    }
}
