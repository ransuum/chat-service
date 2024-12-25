package com.project.chat_service.security.jwt_config;

import com.project.chat_service.models.enums.Roles;
import com.project.chat_service.utils.permission.Permissions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtTokenGenerator {

    private final JwtEncoder jwtEncoder;
    private final Map<Roles, Permissions> permissionsMap;

    public JwtTokenGenerator(JwtEncoder jwtEncoder, List<Permissions> permissions) {
        this.jwtEncoder = jwtEncoder;
        this.permissionsMap = permissions.stream()
                .collect(Collectors.toMap(Permissions::getRole, o -> o));
    }

    public String generateAccessToken(Authentication authentication) {
        String roles = getRolesOfUser(authentication);
        String permissions = String.join(" ", permissionsMap.get(Roles.valueOf(roles)).fillPermissions());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("atquil")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15 , ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("scope", permissions)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private static String getRolesOfUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    public String generateRefreshToken(Authentication authentication) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("atquil")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15 , ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim("scope", "REFRESH_TOKEN")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}