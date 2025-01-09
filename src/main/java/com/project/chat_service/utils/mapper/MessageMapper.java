package com.project.chat_service.utils.mapper;

import com.project.chat_service.models.dto.MessageDto;
import com.project.chat_service.models.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);
    MessageDto toDto(Message message);
}
