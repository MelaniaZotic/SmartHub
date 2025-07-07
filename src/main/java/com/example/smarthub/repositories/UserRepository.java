package com.example.smarthub.repositories;

import com.example.smarthub.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByEnabledFalse();

    boolean existsByEmail(String email);

    Page<User> findByRoles_Name(String roleName, Pageable pageable);
    Page<User> findAllByRolesName(String roleName, Pageable pageable);
}