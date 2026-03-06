package tech.buildrun.springsecurity.shared.DTO;

public record LoginResponse(String accessToken, Long expiresIn) {
}
