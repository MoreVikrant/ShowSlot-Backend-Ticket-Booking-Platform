package com.ShowSlot.service;

import com.ShowSlot.security.JwtKeyConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final Duration lifeTime = Duration.ofMinutes(15);

    public TokenService(JwtEncoder encoder) { this.encoder = encoder; }

    public long expiresInSeconds() { return lifeTime.getSeconds(); }

    public String create(Authentication authentication) {
        Instant now = Instant.now();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replaceFirst("^ROLE_", ""))      // ROLE_ADMIN becomes ADMIN
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(JwtKeyConfig.ISSUER)
                .subject(authentication.getName())           // the email
                .issuedAt(now)
                .expiresAt(now.plus(lifeTime))
                .id(UUID.randomUUID().toString())
                .claim("roles", roles)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}