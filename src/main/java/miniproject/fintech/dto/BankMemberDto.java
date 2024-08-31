package miniproject.fintech.dto;

import lombok.*;
import miniproject.fintech.type.Grade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankMemberDto {

    //@NotNull(message = "ID cannot be null")
    //@Min(value = 1, message = "ID must be greater than 0")
    private Long id;

    //@NotBlank(message = "Name cannot be blank")
    private String name;

    //@NotBlank(message = "Account number is required")
    private String accountNumber;

    private int amount;

    //@NotBlank(message = "Email is required")
    //@Email(message = "Invalid email format")
    private String email;

    //@NotBlank (message = "password cannot be blank")
    private String password;
    private int age;
    private LocalDate birth;
    private long curAmount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private Grade grade;
    private String address;

    private String roles;
    private final List<AccountDto> accounts = new ArrayList<>();
}
