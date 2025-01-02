package com.project.chat_service.service;

import com.project.chat_service.exception.NotFoundException;
import com.project.chat_service.models.dto.AuthResponse;
import com.project.chat_service.models.entity.Users;
import com.project.chat_service.models.enums.TokenType;
import com.project.chat_service.models.request.SignUpRequest;
import com.project.chat_service.repository.UserRepository;
import com.project.chat_service.security.utils.AuthUtils;
import com.project.chat_service.utils.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtils;

    @Value("${admin.email}")
    private String email;

    public UserService(UserRepository usersRepo, PasswordEncoder passwordEncoder, AuthUtils authUtils) {
        this.usersRepo = usersRepo;
        this.passwordEncoder = passwordEncoder;
        this.authUtils = authUtils;
    }

    public AuthResponse registerUser(SignUpRequest signUpRequest, HttpServletResponse httpServletResponse) {
        usersRepo.findByEmail(signUpRequest.email()).ifPresent(users -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        });

        var user = UserMapper.INSTANCE.requestToUser(signUpRequest
                .encodingPassword(passwordEncoder.encode(signUpRequest.password())));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles((user.getEmail().equals(email) ? "ROLE_ADMIN" : "ROLE_USER"));

        Authentication authentication = authUtils.createAuthenticationObject(user);

        String accessToken = authUtils.generateTokenForAccess(authentication);
        String refreshToken = authUtils.generateTokenForRefresh(authentication);

        var savedUserDetails = UserMapper.INSTANCE.UserToDto(usersRepo.save(user));
        authUtils.saveUserRefreshToken(user, refreshToken);

        authUtils.creatRefreshTokenCookie(httpServletResponse, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .userName(savedUserDetails.username())
                .tokenType(TokenType.Bearer)
                .build();
    }

    public Users getUserById(String id) {
        return usersRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

}
