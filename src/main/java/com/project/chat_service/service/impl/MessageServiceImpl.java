package com.project.chat_service.service.impl;

import com.project.chat_service.exception.FieldValidationException;
import com.project.chat_service.models.dto.MessageDto;
import com.project.chat_service.models.entity.Chat;
import com.project.chat_service.models.entity.Message;
import com.project.chat_service.models.request.MessageCreateRequest;
import com.project.chat_service.repository.ChatRepo;
import com.project.chat_service.repository.MessageRepo;
import com.project.chat_service.service.MessageService;
import com.project.chat_service.utils.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final ChatRepo chatRepo;

    public MessageServiceImpl(MessageRepo messageRepo, ChatRepo chatRepo) {
        this.messageRepo = messageRepo;
        this.chatRepo = chatRepo;
    }


    @Override
    public MessageDto createMessage(MessageCreateRequest messageCreateRequest, String chatId) {
        Chat chat = chatRepo.findById(chatId).get();
        return MessageMapper.INSTANCE.toDto(
                messageRepo.save(Message.builder()
                        .createdAt(LocalDateTime.now())
                        .chat(chat)
                        .message(messageCreateRequest.message())
                        .user(chat.getFirstUser())
                        .build())
        );
    }

    @Override
    public MessageDto updateMessage(String messageId, MessageCreateRequest messageCreateRequest) {
        Message message = messageRepo.findById(messageId).get();
        if (!messageCreateRequest.message().isEmpty()) {
            message.setMessage(messageCreateRequest.message());
            return MessageMapper.INSTANCE.toDto(messageRepo.save(message));
        }
        throw new FieldValidationException("Message cannot be empty");
    }

    @Override
    public List<MessageDto> getAllMessagesFromChat(String chatId) {
        return chatRepo.findById(chatId).get().getMessages()
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(MessageMapper.INSTANCE::toDto)
                .toList();
    }

    @Override public void deleteMessage(String messageId) {
        messageRepo.deleteById(messageId);
    }

    @Override public String getMessage(String messageId) {
        return messageRepo.findById(messageId).get().getMessage();
    }
}
