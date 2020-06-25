package com.team2.laps.repository;

import java.util.Optional;

import com.team2.laps.model.Role;
import com.team2.laps.model.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
