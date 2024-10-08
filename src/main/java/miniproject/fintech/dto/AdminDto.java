package miniproject.fintech.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {

    private Long id;
    private String adminId;

    private String name;
    private String password;
    private String email;

    private boolean superAdmin;
    private String roles;
}
