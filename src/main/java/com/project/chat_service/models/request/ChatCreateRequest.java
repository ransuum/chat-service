package com.project.chat_service.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatCreateRequest(
        @Size(max = 24, message = "Username is too long")
        @NotBlank(message = "username is blank")
        String usernameTo,

        @NotBlank(message = "inout some text")
        @Size(max = 125)
        String message) { }
