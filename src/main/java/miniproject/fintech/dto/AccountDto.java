package miniproject.fintech.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.type.AccountStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String accountNumber;
    private String name;
    private long amount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private BankMember bankMember;
    private AccountStatus accountStatus;;
}