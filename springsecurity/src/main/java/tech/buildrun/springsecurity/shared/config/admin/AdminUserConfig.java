package tech.buildrun.springsecurity.shared.config.admin;


import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import tech.buildrun.springsecurity.shared.service.UserService;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final UserService userService;


    public AdminUserConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        userService.ensureAdminUser();
    }
}
