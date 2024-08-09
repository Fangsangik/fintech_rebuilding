package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miniproject.fintech.domain.Account;
import miniproject.fintech.type.TransferStatus;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true) //setter 사용을 줄여보기 위해 사용
@AllArgsConstructor
public class TransferDto {

    private Long id;

    private long transferAmount;
    private LocalDateTime transferAt;

    private Long sourceAccountId;
    private Long destinationAccountId;
    private TransferStatus transferStatus;

    private String message;
}
