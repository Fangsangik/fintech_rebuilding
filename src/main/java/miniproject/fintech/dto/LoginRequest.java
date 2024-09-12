package miniproject.fintech.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String password;

    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
