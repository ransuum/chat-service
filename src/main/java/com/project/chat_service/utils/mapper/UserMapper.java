package com.project.chat_service.utils.mapper;

import com.project.chat_service.models.dto.UserDto;
import com.project.chat_service.models.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

//    @Mapping(source = "firstname", target = "firstname")
//    @Mapping(source = "lastname", target = "lastname")
//    @Mapping(source = "username", target = "username")
//    @Mapping(source = "email", target = "email")
//    @Mapping(source = "address", target = "address")
    UserDto UserToDto(Users user);
}
