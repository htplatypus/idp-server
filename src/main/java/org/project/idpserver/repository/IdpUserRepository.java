package org.project.idpserver.repository;

import org.project.idpserver.entity.IdpUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdpUserRepository extends JpaRepository<IdpUser, Long> {
    IdpUser findByUsername(String username);
    Optional<IdpUser> findByEmail(String email);
}