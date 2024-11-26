package org.project.idpserver.utility;

import org.project.idpserver.service.IdpUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DbInit {

    @Bean
    CommandLineRunner initDatabase(IdpUserService idpUserService) {
        return args -> {
            idpUserService.saveUser("admin", "password", "ADMIN", "admin@example.com");
            idpUserService.saveUser("user", "password", "USER", "user@example.com");
        };
    }
}
