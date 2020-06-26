package com.team2.laps.service;

import java.util.List;

import com.team2.laps.model.Leave;
import com.team2.laps.model.User;
import com.team2.laps.repository.LeaveRepository;
import com.team2.laps.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LeaveService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);

    @Autowired
    LeaveRepository leaveRepository;

    @Autowired
    UserRepository userRepository;

    public List<Leave> getLeaveByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        return leaveRepository.findByUser(user);
    }

    public boolean createOrUpdateLeave(Leave leave) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        leave.setUser(user);
        if (leaveRepository.save(leave) != null)
            return true;
        else
            return false;
    }

    public void deleteLeave(String leaveId) {
        logger.error(leaveId);
        leaveRepository.deleteById(leaveId);
    }
}
