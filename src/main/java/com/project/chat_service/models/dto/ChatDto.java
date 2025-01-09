package com.project.chat_service.models.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatDto(String id, UserDto firstUser, UserDto secondUser, List<MessageDto> messages, LocalDateTime createdAt) { }
