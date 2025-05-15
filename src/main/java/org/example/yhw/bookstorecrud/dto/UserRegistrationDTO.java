package org.example.yhw.bookstorecrud.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRegistrationDTO {

    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;

    private String role = "ROLE_USER";
}
