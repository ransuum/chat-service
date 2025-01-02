package com.project.chat_service.service;

import com.project.chat_service.exception.NotFoundException;
import com.project.chat_service.models.dto.AuthResponse;
import com.project.chat_service.models.entity.Users;
import com.project.chat_service.models.enums.TokenType;
import com.project.chat_service.models.request.SignInRequest;
import com.project.chat_service.models.request.SignUpRequest;
import com.project.chat_service.repository.UserRepository;
import com.project.chat_service.security.utils.AuthUtils;
import com.project.chat_service.utils.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
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

    public AuthResponse getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            var userInfoEntity = usersRepo.findByEmail(authentication.getName())
                    .orElseThrow(() -> new NotFoundException("USER NOT FOUND"));

            String accessToken = authUtils.generateTokenForAccess(authentication);
            String refreshToken = authUtils.generateTokenForRefresh(authentication);
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

    public AuthResponse authenticate(SignInRequest signInRequest, HttpServletResponse response) {
        try {
            Users user = usersRepo.findByEmail(signInRequest.email())
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + signInRequest.email()));

            if (!passwordEncoder.matches(signInRequest.password(), user.getPassword())) {
                throw new BadCredentialsException("Invalid password.");
            }

            Authentication authentication = authUtils.createAuthenticationObject(user);

            String accessToken = authUtils.generateTokenForAccess(authentication);
            String refreshToken = authUtils.generateTokenForRefresh(authentication);

            authUtils.saveUserRefreshToken(user, refreshToken);

            authUtils.creatRefreshTokenCookie(response, refreshToken);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(15 * 60)
                    .userName(user.getEmail())
                    .tokenType(TokenType.Bearer)
                    .build();

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.error("Authentication failed for user: {}", email, e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    public AuthResponse registerUser(SignUpRequest signUpRequest, HttpServletResponse httpServletResponse) {
        usersRepo.findByEmail(signUpRequest.email()).ifPresent(users -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        });

        var user = UserMapper.INSTANCE.requestToUser(signUpRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
