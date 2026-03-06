package tech.buildrun.springsecurity.shared.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.shared.DTO.CompleteProfileDTO;
import tech.buildrun.springsecurity.shared.DTO.UserProfileResponse;
import tech.buildrun.springsecurity.shared.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Rota GET /api/me
     * O React vai chamar esta rota para validar o cookie e pegar os dados da tela (Nome, etc).
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        UserProfileResponse profile = userService.getUserProfile(userId);

        return ResponseEntity.ok(profile);
    }


    @PutMapping("/me/complete-profile")
    public ResponseEntity<String> completeProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CompleteProfileDTO dto) {

        UUID userId = UUID.fromString(jwt.getSubject());

        userService.completeUserProfile(userId, dto);

        return ResponseEntity.ok("Perfil completado com sucesso!");
    }

}