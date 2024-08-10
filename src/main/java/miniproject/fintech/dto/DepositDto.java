package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import miniproject.fintech.domain.Account;
import miniproject.fintech.type.DepositStatus;
import java.time.LocalDateTime;


@Getter
@Builder(toBuilder = true) //setter 사용을 줄여보기 위해 사용
@NoArgsConstructor
@AllArgsConstructor
public class DepositDto {

    private Long id;

    private long depositAmount;
    private LocalDateTime depositAt;

    private Long accountId;

    private DepositStatus depositStatus;

    private String message;
}
