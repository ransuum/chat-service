package com.project.chat_service.models.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignInRequest(
        @Valid
        @Email(message = "Isn't email")
        @NotBlank(message = "Email is blank")
        @Size(max = 40, message = "Email is too long")
        String email,

        @Valid
        @NotBlank(message = "Password is blank")
        @Size(min = 8, max = 30, message = "Password size should be from 9 to 30 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
        String password
) {
    public static SignInRequest empty() {
        return new SignInRequest("", "");
    }
}
