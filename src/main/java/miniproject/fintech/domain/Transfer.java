package miniproject.fintech.domain;

import lombok.*;
import miniproject.fintech.type.*;

import javax.persistence.*;
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

    private Long sourceAccountId;
    private Long destinationAccountId;

    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;

    private String message;

}
