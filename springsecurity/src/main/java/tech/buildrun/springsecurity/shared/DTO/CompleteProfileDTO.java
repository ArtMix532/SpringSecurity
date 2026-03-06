package tech.buildrun.springsecurity.shared.DTO;

import jakarta.validation.constraints.NotBlank;

public record CompleteProfileDTO(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O CPF é obrigatório")
        String cpf,

        @NotBlank(message = "O telefone é obrigatório")
        String phone
) {}