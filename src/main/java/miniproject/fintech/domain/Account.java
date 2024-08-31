package miniproject.fintech.domain;

import jakarta.persistence.*;
import lombok.*;
import miniproject.fintech.type.AccountStatus;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private String name;
    private long amount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "bank_member_id")
    private BankMember bankMember;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deposit> deposits = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> sentTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> receivedTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();
}
