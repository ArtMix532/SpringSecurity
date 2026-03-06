package tech.buildrun.springsecurity.shared.DTO;

import tech.buildrun.springsecurity.shared.enums.Role;
import java.util.Set;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String name,
        String cpf,
        String phone,
        String provider,
        Set<Role> roles
) {
}