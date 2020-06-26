package com.team2.laps.repository;

import java.util.List;

import com.team2.laps.model.Leave;
import com.team2.laps.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, String> {
    List<Leave> findByUser(User user);
}
