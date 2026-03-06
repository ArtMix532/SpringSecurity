package tech.buildrun.springsecurity.shared.DTO;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "O Token do Google é obrigatório")
        String accessToken
) {}