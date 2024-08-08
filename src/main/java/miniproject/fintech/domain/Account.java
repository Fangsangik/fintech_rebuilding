package miniproject.fintech.domain;

import lombok.*;
import miniproject.fintech.type.AccountStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private long amount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "bank_member_id")
    private BankMember bankMember;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deposit> deposits = new HashSet<>();

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transfer> sentTransfers = new HashSet<>();

    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transfer> receivedTransfers = new HashSet<>();
}
