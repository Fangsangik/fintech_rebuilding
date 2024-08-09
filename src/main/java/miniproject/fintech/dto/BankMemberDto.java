package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miniproject.fintech.type.Grade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BankMemberDto {
    private Long id;

    private String name;
    private String accountNumber;
    private int amount;
    private String email;
    private String password;
    private int age;
    private LocalDate birth;
    private long curAmount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private Grade grade;
    private String address;

    private List<AccountDto> accounts;
}
