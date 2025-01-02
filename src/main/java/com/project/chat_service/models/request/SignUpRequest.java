package com.project.chat_service.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "firstname is blank")
        String firstname,
        @Size(max = 24, message = "Username is too long")
        @NotBlank(message = "username is blank")
        String username,
        @Valid
        @NotBlank(message = "Password is blank")
        @Size(min = 8, max = 30, message = "Password size should be from 9 to 30 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
        String password,
        @Valid
        @Email(message = "Isn't email")
        @NotBlank(message = "Email is blank")
        @Size(max = 40, message = "Email is too long")
        String email,
        @NotBlank(message = "lastname is blank")
        @Pattern(regexp = "^[\\p{L}\\p{M} ,.'-]+$", message = "Incorrect last name")
        String lastname,
        @NotBlank(message = "address is blank")
        @JsonProperty("address")
        String address,
        @Valid
        @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number")
        String phone) {

        public SignUpRequest encodingPassword(String new_password) {
                return new SignUpRequest(firstname, username, password, email, lastname, new_password, phone);
        }

        public static SignUpRequest empty() {
                return new SignUpRequest("", "", "", "", "", "", "");
        }
}
