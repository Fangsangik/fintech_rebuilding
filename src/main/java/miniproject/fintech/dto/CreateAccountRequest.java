package miniproject.fintech.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//BankMember와 AccountDto를 하나로 묶어서 @RequestBody로 한번에 처리
public class CreateAccountRequest {

    @Valid
    @NotNull(message = "BankMemberDto cannot be null")
    private BankMemberDto bankMemberDto;

    @Valid
    @NotNull(message = "AccountDto cannot be null")
    private AccountDto accountDto;

}
