package com.hamy.devflow.auth.dto;

import com.hamy.devflow.user.UserRole;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private Long userId;
    private String fullName;
    private String email;
    private UserRole role;
    private String accessToken;
    private String message;
    private String refreshToken;
}
