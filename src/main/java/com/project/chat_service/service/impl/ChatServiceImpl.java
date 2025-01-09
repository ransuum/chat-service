package com.project.chat_service.service.impl;

import com.project.chat_service.exception.NotFoundException;
import com.project.chat_service.models.dto.ChatDto;
import com.project.chat_service.models.entity.Chat;
import com.project.chat_service.models.entity.Message;
import com.project.chat_service.models.entity.Users;
import com.project.chat_service.models.request.ChatCreateRequest;
import com.project.chat_service.repository.ChatRepo;
import com.project.chat_service.repository.MessageRepo;
import com.project.chat_service.repository.UserRepository;
import com.project.chat_service.service.ChatService;
import com.project.chat_service.utils.authentication_manager.UserConfiguration;
import com.project.chat_service.utils.mapper.ChatMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;
    private final UserRepository userRepo;
    private final UserConfiguration userConfiguration;

    public ChatServiceImpl(ChatRepo chatRepo, MessageRepo messageRepo, UserRepository userRepo, UserConfiguration userConfiguration) {
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.userConfiguration = userConfiguration;
    }

    @Override
    public ChatDto createChatWithUser(ChatCreateRequest chatCreateRequest) {
        Users current = userConfiguration.currentUser();
        Chat chat = Chat.builder()
                .firstUser(current)
                .secondUser(userRepo.findByUsername(chatCreateRequest.usernameTo())
                        .orElseThrow(() -> new NotFoundException("User " + chatCreateRequest.usernameTo() + " not found")))
                .build();

        Message message = Message.builder()
                .chat(chatRepo.save(chat))
                .user(current)
                .message(chatCreateRequest.message())
                .createdAt(LocalDateTime.now())
                .build();

        return ChatMapper.INSTANCE.toDto(messageRepo.save(message).getChat());
    }

    @Override public ChatDto findById(String chatId) {
        return ChatMapper.INSTANCE.toDto(chatRepo.findById(chatId)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override public List<ChatDto> findAllChats() {
        return chatRepo.findAll()
                .stream()
                .sorted(Comparator.comparing(Chat::getCreatedAt))
                .map(ChatMapper.INSTANCE::toDto)
                .toList();
    }

    @Override public void deleteChat(String chatId) {
        chatRepo.deleteById(chatId);
    }

    @Override public ChatDto archiveChat(String chatId) {
        return null;
    }
}
