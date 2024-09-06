package miniproject.fintech.domain;

import jakarta.persistence.*;
import lombok.*;
import miniproject.fintech.type.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long transferAmount;
    private LocalDateTime transferAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private String sourceAccountNumber;
    private String destinationAccountNumber;

    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;  // 1:1 관계

    private String message;

}
