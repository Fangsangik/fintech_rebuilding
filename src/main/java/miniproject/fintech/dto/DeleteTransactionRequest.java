package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTransactionRequest {
    private TransactionDto transactionDto;
    private BankMemberDto bankMemberDto;
}