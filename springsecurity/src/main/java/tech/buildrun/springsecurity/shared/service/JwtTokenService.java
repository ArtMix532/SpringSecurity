package tech.buildrun.springsecurity.shared.service;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.shared.DTO.LoginResponse;
import tech.buildrun.springsecurity.shared.models.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;

    public JwtTokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse generateToken(User user) {
        Instant now = Instant.now();
        long expiresIn = 3600L;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("springsecurity-app")
                .issuedAt(now)
                .expiresAt(now.plus(expiresIn, ChronoUnit.SECONDS))
                .subject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles())
                .build();

        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(tokenValue, expiresIn);
    }
}
