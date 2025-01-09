package com.project.chat_service.service.impl;

import com.project.chat_service.exception.NotFoundException;
import com.project.chat_service.models.dto.AuthResponse;
import com.project.chat_service.models.dto.UserDto;
import com.project.chat_service.models.entity.Users;
import com.project.chat_service.models.enums.TokenType;
import com.project.chat_service.models.request.SignUpRequest;
import com.project.chat_service.repository.UserRepository;
import com.project.chat_service.security.utils.AuthUtils;
import com.project.chat_service.service.UserService;
import com.project.chat_service.utils.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtils;

    @Value("${admin.email}")
    private String email;

    public UserServiceImpl(UserRepository usersRepo, PasswordEncoder passwordEncoder, AuthUtils authUtils) {
        this.usersRepo = usersRepo;
        this.passwordEncoder = passwordEncoder;
        this.authUtils = authUtils;
    }

    @Override
    public AuthResponse getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            var userInfoEntity = usersRepo.findByEmail(authentication.getName())
                    .orElseThrow(() -> new NotFoundException("USER NOT FOUND"));

            final String accessToken = authUtils.generateTokenForAccess(authentication);
            final String refreshToken = authUtils.generateTokenForRefresh(authentication);
            authUtils.saveUserRefreshToken(userInfoEntity, refreshToken);
            authUtils.creatRefreshTokenCookie(response, refreshToken);
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(15 * 60)
                    .userName(userInfoEntity.getUsername())
                    .tokenType(TokenType.Bearer)
                    .build();


        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }

    @Override
    public AuthResponse registerUser(SignUpRequest signUpRequest, HttpServletResponse httpServletResponse) {
        usersRepo.findByEmail(signUpRequest.email()).ifPresent(users -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        });

        var user = UserMapper.INSTANCE.requestToUser(signUpRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles((user.getEmail().equals(email) ? "ROLE_ADMIN" : "ROLE_USER"));

        final Authentication authentication = authUtils.createAuthenticationObject(user);

        final String accessToken = authUtils.generateTokenForAccess(authentication);
        final String refreshToken = authUtils.generateTokenForRefresh(authentication);

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

    @Override public UserDto getUserById(String id) {
        return UserMapper.INSTANCE.UserToDto(usersRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

}
