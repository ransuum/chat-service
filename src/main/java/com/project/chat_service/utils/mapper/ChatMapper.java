package com.project.chat_service.utils.mapper;

import com.project.chat_service.models.dto.ChatDto;
import com.project.chat_service.models.entity.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = MessageMapper.class)
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);
    ChatDto toDto(Chat chat);
}
