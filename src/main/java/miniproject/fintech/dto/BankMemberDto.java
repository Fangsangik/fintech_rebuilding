package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import miniproject.fintech.type.Grade;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankMemberDto {
    private Long id;

    @NotBlank (message = "name cannot be blank")
    private String name;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    private int amount;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank (message = "password cannot be blank")
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
