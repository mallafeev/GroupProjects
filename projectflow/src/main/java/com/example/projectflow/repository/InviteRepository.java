package com.example.projectflow.repository;

import com.example.projectflow.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByCode(String code);
    void deleteByCode(String code);
}