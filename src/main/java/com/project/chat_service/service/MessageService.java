package com.project.chat_service.service;

import com.project.chat_service.models.dto.MessageDto;
import com.project.chat_service.models.request.MessageCreateRequest;

import java.util.List;

public interface MessageService {
    MessageDto createMessage(MessageCreateRequest messageCreateRequest, String chatId);
    MessageDto updateMessage(String messageId, MessageCreateRequest messageCreateRequest);
    List<MessageDto> getAllMessagesFromChat(String chatId);
    void deleteMessage(String messageId);
    String getMessage(String messageId);
}
