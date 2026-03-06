package tech.buildrun.springsecurity.shared.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.buildrun.springsecurity.shared.DTO.GoogleLoginRequest;
import tech.buildrun.springsecurity.shared.DTO.LoginResponse;
import tech.buildrun.springsecurity.shared.enums.AuthProvider;
import tech.buildrun.springsecurity.shared.enums.Role;
import tech.buildrun.springsecurity.shared.models.StandardUser;
import tech.buildrun.springsecurity.shared.models.User;
import tech.buildrun.springsecurity.shared.repository.StandardUserRepository;
import tech.buildrun.springsecurity.shared.repository.UserRepository;

import java.util.Map;
import java.util.Set;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final StandardUserRepository standardUserRepository;
    private final JwtTokenService jwtTokenService;

    public GoogleAuthService(UserRepository userRepository,
                             StandardUserRepository standardUserRepository,
                             JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.standardUserRepository = standardUserRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse autenticarComGoogle(GoogleLoginRequest request) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(request.accessToken());
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> payload = response.getBody();

            if (payload == null || !payload.containsKey("email")) {
                throw new IllegalArgumentException("Token do Google inválido ou sem permissão de e-mail.");
            }

            String email = (String) payload.get("email");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                StandardUser newUser = new StandardUser();
                newUser.setEmail(email);
                newUser.setPassword(null);
                newUser.setProvider(AuthProvider.GOOGLE);
                newUser.setEmailVerified(true);
                newUser.setRoles(Set.of(Role.USER));
                return standardUserRepository.save(newUser);
            });

            return jwtTokenService.generateToken(user);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao autenticar com o Google", e);
        }
    }
}