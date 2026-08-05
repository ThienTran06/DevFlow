package com.hamy.devflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogoutRequest {
    @NotBlank(message = "Token is required")
    private String refreshToken;
}
