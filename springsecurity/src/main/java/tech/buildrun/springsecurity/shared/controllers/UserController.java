package tech.buildrun.springsecurity.shared.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.shared.DTO.LoginResponse;
import tech.buildrun.springsecurity.shared.DTO.CreateUserDTO;
import tech.buildrun.springsecurity.shared.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/admin/init")
    public ResponseEntity<Void> initAdminUser() {
        userService.ensureAdminUser();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/public")
    public ResponseEntity<String> createUserPublic(@RequestBody CreateUserDTO createUserDTO) {
        userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Conta criada. Verificação de e-mail pendente.");
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDTO createUserDTO) {
        userService.createUser(createUserDTO);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/public/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyUserEmail(token);
            return ResponseEntity.ok("E-mail verificado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/public/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) {
        try {
            userService.resendVerificationEmail(email);
            return ResponseEntity.ok("E-mail reenviado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
