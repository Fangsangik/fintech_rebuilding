package miniproject.fintech.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import miniproject.fintech.type.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
//단일 계좌 입금
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long depositAmount;
    private LocalDateTime depositAt;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private DepositStatus depositStatus;

    private String message;
}
