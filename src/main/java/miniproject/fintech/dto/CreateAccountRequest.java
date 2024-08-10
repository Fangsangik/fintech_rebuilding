package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//BankMember와 AccountDto를 하나로 묶어서 @RequestBody로 한번에 처리
public class CreateAccountRequest {

    private BankMemberDto bankMemberDto;
    private AccountDto accountDto;
}
