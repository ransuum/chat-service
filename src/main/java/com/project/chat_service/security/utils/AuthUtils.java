package com.project.chat_service.security.utils;

import com.project.chat_service.exception.NotFoundException;
import com.project.chat_service.models.dto.AuthResponse;
import com.project.chat_service.models.entity.RefreshToken;
import com.project.chat_service.models.entity.Users;
import com.project.chat_service.models.enums.TokenType;
import com.project.chat_service.models.request.SignInRequest;
import com.project.chat_service.repository.RefreshTokenRepo;
import com.project.chat_service.security.jwt_config.JwtTokenGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Component
public class AuthUtils {
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final AuthenticationManager authenticationManager;

    public AuthUtils(RefreshTokenRepo refreshTokenRepo, JwtTokenGenerator jwtTokenGenerator,
                     AuthenticationManager authenticationManager) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.authenticationManager = authenticationManager;
    }

    public Cookie creatRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        final Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }

    public void saveUserRefreshToken(Users users, String refreshToken) {
        var refreshTokenEntity = RefreshToken.builder()
                .user(users)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();
        refreshTokenRepo.save(refreshTokenEntity);
    }

    public void auth(SignInRequest sign) {
        final var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(sign.email(), sign.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {

        if (!authorizationHeader.startsWith(TokenType.Bearer.name())) {
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please verify your token type");
        }

        final String refreshToken = authorizationHeader.substring(7);

        final var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
                .filter(tokens -> !tokens.isRevoked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));

        Users users = refreshTokenEntity.getUser();

        final Authentication authentication = createAuthenticationObject(users);

        final String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .userName(users.getUsername())
                .tokenType(TokenType.Bearer)
                .build();
    }

    public Authentication createAuthenticationObject(Users users) {
        final String username = users.getEmail();
        final String password = users.getPassword();
        final String roles = users.getRoles();

        final String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
    }

    public String generateTokenForAccess(Authentication authentication) {
        return jwtTokenGenerator.generateAccessToken(authentication);
    }

    public String generateTokenForRefresh(Authentication authentication) {
        return jwtTokenGenerator.generateRefreshToken(authentication);
    }
}
