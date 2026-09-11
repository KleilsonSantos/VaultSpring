package io.github.kleilsonsantos.security.vaultspring.security;

import io.github.kleilsonsantos.security.vaultspring.config.JwtProperties;
import io.github.kleilsonsantos.security.vaultspring.dto.LoginResponse;
import io.github.kleilsonsantos.security.vaultspring.entity.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Issues signed access tokens for authenticated users.
 */
@Service
public class JwtService {

    /**
     * JWT encoder bean.
     */
    private final JwtEncoder jwtEncoder;

    /**
     * Token lifetime and secret metadata.
     */
    private final JwtProperties jwtProperties;

    /**
     * @param jwtEncoder    encoder bean
     * @param jwtProperties JWT settings
     */
    public JwtService(final JwtEncoder jwtEncoder, final JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Builds a Bearer login response for the given user.
     *
     * @param user authenticated user
     * @return access token payload
     */
    public LoginResponse createLoginResponse(final User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtProperties.getExpirationSeconds());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("uid", user.getId())
                .claim("name", user.getName())
                .claim("roles", List.of(user.getRole().toAuthority()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", jwtProperties.getExpirationSeconds());
    }
}
