package org.project.idpserver.service;

import org.project.idpserver.entity.Client;
import org.project.idpserver.repository.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientDetailsService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Client saveClient(String clientId, String clientSecret, String role) {
        return clientRepository.save(new Client(clientId, passwordEncoder.encode(clientSecret), role));
    }

    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        Client client = clientRepository.findByClientId(clientId);
        if (client == null) {
            throw new UsernameNotFoundException("Client not found");
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(client.getClientId())
                .password(client.getClientSecret())
                .roles(client.getRole())
                .build();
    }
}
