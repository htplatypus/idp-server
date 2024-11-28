package org.project.idpserver.config;

import org.project.idpserver.service.ClientDetailsService;
import org.project.idpserver.service.IdpUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class AuthProviderConfig {

    private final IdpUserService idpUserService;
    private final ClientDetailsService clientDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthProviderConfig(IdpUserService idpUserService, ClientDetailsService clientDetailsService, PasswordEncoder passwordEncoder) {
        this.idpUserService = idpUserService;
        this.clientDetailsService = clientDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(idpUserService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider clientAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(clientDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // without this we get a stack overflow as spring tries to search for auth provider and cant choose
        return new ProviderManager(List.of(userAuthenticationProvider(), clientAuthenticationProvider()));
    }
}
