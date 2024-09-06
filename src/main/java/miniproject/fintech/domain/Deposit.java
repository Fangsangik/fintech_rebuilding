package miniproject.fintech.domain;

import jakarta.persistence.*;
import lombok.*;
import miniproject.fintech.type.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) //setter 사용을 줄여보기 위해 사용
@Entity
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

    private String sourceAccountNumber;
    private String destinationAccountNumber;

    @Enumerated(EnumType.STRING)
    private DepositStatus depositStatus;

    private String message;
}
