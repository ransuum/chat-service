package com.project.chat_service.service;

import com.project.chat_service.models.dto.AuthResponse;
import com.project.chat_service.models.dto.UserDto;
import com.project.chat_service.models.request.SignUpRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface UserService {
    AuthResponse getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response);
    AuthResponse registerUser(SignUpRequest signUpRequest, HttpServletResponse httpServletResponse);
    UserDto getUserById(String id);
}
