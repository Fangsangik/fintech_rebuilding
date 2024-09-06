package miniproject.fintech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import miniproject.fintech.type.TransferStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true) //setter 사용을 줄여보기 위해 사용
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    private Long id;

    private long transferAmount;
    private LocalDateTime transferAt;

    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private TransferStatus transferStatus;

    private BankMemberDto bankMemberDto;
    private Long transactionId;
    private String message;
}
