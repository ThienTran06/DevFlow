package com.hamy.devflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    @Size(max=36)
    private String token;
    @Size(min=6,max=100,message = "New password must have from 6 to 100 characters")
    @NotBlank(message = "You need a new password?")
    private String newPassword;
}
