package tech.buildrun.springsecurity.shared.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tech.buildrun.springsecurity.shared.enums.AuthProvider;

public record CreateUserDTO(

        @NotBlank
        @Email
        String email,

        @Size(min = 8, max = 64, message = "A senha deve conter entre 8 e 64 caracteres")
        String password,

        AuthProvider provider,

        String name,

        String phone,

        String cpf

) {}
