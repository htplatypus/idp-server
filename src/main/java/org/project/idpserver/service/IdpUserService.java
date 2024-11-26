package org.project.idpserver.service;

import org.project.idpserver.entity.IdpUser;
import org.project.idpserver.repository.IdpUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// implement UserDetailsService to bridge custom user db with spring security
@Service
public class IdpUserService implements UserDetailsService {

    private final IdpUserRepository idpUserRepository;
    private final PasswordEncoder passwordEncoder;

    public IdpUserService(IdpUserRepository idpUserRepository, PasswordEncoder passwordEncoder) {
        this.idpUserRepository = idpUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public IdpUser saveUser(String username, String password, String role, String email) {
        IdpUser user = new IdpUser(username, passwordEncoder.encode(password), role, email);
        return idpUserRepository.save(user);
    }

    public Optional<IdpUser> findByUsername(String username) {
        return Optional.ofNullable(idpUserRepository.findByUsername(username));
    }

    public void deleteUser(Long id) {
        idpUserRepository.deleteById(id);
    }

    public Iterable<IdpUser> getAllUsers() {
        return idpUserRepository.findAll();
    }

    // SPRING SECURITY

    @Override
    // implement UserDetailsService method to load user by username
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        IdpUser user = idpUserRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // map custom user object to spring security UserDetails object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // make sure this is already hashed
                .roles(user.getRole())        // (must have "ROLE_" prefix)
                .build();
    }
}
