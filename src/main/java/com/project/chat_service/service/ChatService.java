package com.project.chat_service.service;

import com.project.chat_service.models.dto.ChatDto;
import com.project.chat_service.models.request.ChatCreateRequest;

import java.util.List;

public interface ChatService {
    ChatDto createChatWithUser(ChatCreateRequest chatCreateRequest);
    ChatDto findById(String chatId);
    List<ChatDto> findAllChats();
    void deleteChat(String chatId);
    ChatDto archiveChat(String chatId);
}
