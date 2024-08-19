package miniproject.fintech.dto;

import lombok.*;
import miniproject.fintech.type.AccountStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    @NotNull
    @Min(1)
    private Long id;

    @NotNull(message = "null이면 안됩니다.")
    private String accountNumber;

    @NotNull(message = "null이면 안됩니다.")
    private String name;

    @NotNull(message = "null이면 안됩니다.")
    private long amount;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @NotNull(message = "null이면 안됩니다.")
    private AccountStatus accountStatus;

    private List<DepositDto> deposits = new ArrayList<>();
    private List<TransferDto> sentTransfers = new ArrayList<>();
    private List<TransferDto> receivedTransfers = new ArrayList<>();
    private List<TransactionDto> transactions = new ArrayList<>();
}