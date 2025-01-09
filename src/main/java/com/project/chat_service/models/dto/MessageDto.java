package com.project.chat_service.models.dto;

import java.time.LocalDateTime;

public record MessageDto(String id, String message, UserDto user, ChatDto chat, LocalDateTime createdAt) { }
