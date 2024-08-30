package miniproject.fintech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import miniproject.fintech.type.TransferStatus;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true) //setter 사용을 줄여보기 위해 사용
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {

    @NotNull
    @Min(1)
    private Long id;

    private long transferAmount;
    private LocalDateTime transferAt;

    private long sourceAccountId;
    private long destinationAccountId;
    private TransferStatus transferStatus;

    private String message;
}
