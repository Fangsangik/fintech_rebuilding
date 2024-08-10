package miniproject.fintech.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import miniproject.fintech.domain.BankMember;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    private TransactionDto transactionDto;
    private BankMemberDto bankMemberDto;

}
