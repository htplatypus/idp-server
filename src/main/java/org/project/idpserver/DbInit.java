package org.project.idpserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DbInit {

    @Bean
    CommandLineRunner initDatabase(UserService userService) {
        return args -> {
            userService.saveUser("admin", "password", "ADMIN", "admin@example.com");
            userService.saveUser("user", "password", "USER", "user@example.com");
        };
    }
}
