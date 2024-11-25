package org.project.idpserver;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<IdpUser, Long> {
    IdpUser findByUsername(String username);
}