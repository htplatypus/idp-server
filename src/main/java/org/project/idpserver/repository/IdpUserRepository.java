package org.project.idpserver.repository;

import org.project.idpserver.entity.IdpUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdpUserRepository extends JpaRepository<IdpUser, Long> {
    IdpUser findByUsername(String username);
}