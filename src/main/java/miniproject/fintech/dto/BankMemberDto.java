package miniproject.fintech.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private Long id;
    private String userId;
    private String name;
    private int amount;
    private String email;
    private String password;
    private int age;
    private LocalDate birth;
    private long curAmount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private String roles;
    private Grade grade;
    private String address;
    private boolean isActive;
}
