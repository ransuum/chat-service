package com.project.chat_service.utils.mapper;

import com.project.chat_service.models.dto.UserDto;
import com.project.chat_service.models.entity.Users;
import com.project.chat_service.models.request.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "firstname", expression = "java(user.getFirstname())")
    @Mapping(target = "lastname", expression = "java(user.getLastname())")
    @Mapping(target = "username", expression = "java(user.getUsername())")
    @Mapping(target = "email", expression = "java(user.getEmail())")
    @Mapping(target = "address", expression = "java(user.getAddress())")
    @Mapping(target = "phone", expression = "java(user.getPhone())")
    UserDto UserToDto(Users user);

    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "phone", target = "phone")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Users requestToUser(SignUpRequest signUpRequest);
}
