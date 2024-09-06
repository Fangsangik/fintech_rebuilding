package miniproject.fintech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import miniproject.fintech.type.DepositStatus;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder(toBuilder = true) //setter 사용을 줄여보기 위해 사용
@NoArgsConstructor
@AllArgsConstructor
public class DepositDto {
    @NotNull
    @Min(1)
    private Long id;

    private long depositAmount;
    private LocalDateTime depositAt;

    private String sourceAccountNumber;
    private String destinationAccountNumber;

    private DepositStatus depositStatus;

    private BankMemberDto bankMemberDto;
    private Long transactionId;

    private String message;
}
