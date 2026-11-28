package com.hamy.devflow.auth.dto;

import jakarta.validation.constraints.Email;
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
public class RegisterRequest {
    @NotBlank(message = "Fullname is required")
    @Size(max=255)
    private String fullName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @Size(max=254)
    private String email;
    @NotBlank(message = "Password can not be blank")
    @Size(min = 6,max=100,message = "Password must be between 6 characters and 100")
    private String password;
}
