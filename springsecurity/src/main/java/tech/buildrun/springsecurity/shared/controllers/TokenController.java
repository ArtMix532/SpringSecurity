package tech.buildrun.springsecurity.shared.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.shared.DTO.GoogleLoginRequest;
import tech.buildrun.springsecurity.shared.DTO.LoginRequest;
import tech.buildrun.springsecurity.shared.DTO.LoginResponse;
import tech.buildrun.springsecurity.shared.service.GoogleAuthService;
import tech.buildrun.springsecurity.shared.service.UserService;

@RestController
@RequestMapping("/api")
public class TokenController {

    private final UserService userService;
    private final GoogleAuthService googleAuthService;

    public TokenController(UserService userService, GoogleAuthService googleAuthService) {
        this.userService = userService;
        this.googleAuthService = googleAuthService;
    }

    private HttpHeaders criarHeaderComCookie(String token, long expiresIn) {
        ResponseCookie cookie = ResponseCookie.from("jwtToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(expiresIn)
                .sameSite("Strict")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.autenticarUsuario(loginRequest);
        HttpHeaders headers = criarHeaderComCookie(response.accessToken(), response.expiresIn());
        return ResponseEntity.ok().headers(headers).body(response);
    }

    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody @Valid GoogleLoginRequest request) {
        LoginResponse response = googleAuthService.autenticarComGoogle(request);
        HttpHeaders headers = criarHeaderComCookie(response.accessToken(), response.expiresIn());
        return ResponseEntity.ok().headers(headers).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}