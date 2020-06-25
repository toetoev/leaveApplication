package com.example.springbootsecurity.repository;

import java.util.Optional;

import com.example.springbootsecurity.model.Role;
import com.example.springbootsecurity.model.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
