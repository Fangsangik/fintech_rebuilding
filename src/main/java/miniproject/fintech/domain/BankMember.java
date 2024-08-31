package miniproject.fintech.domain;

import jakarta.persistence.*;
import lombok.*;
import miniproject.fintech.type.Grade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private String roles;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    private String address;

    private boolean isActive;

    @OneToMany(mappedBy = "bankMember",fetch = FetchType.LAZY ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>(); // 빈 리스트로 초기화

    @OneToMany(mappedBy = "bankMember", fetch = FetchType.LAZY ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

}
