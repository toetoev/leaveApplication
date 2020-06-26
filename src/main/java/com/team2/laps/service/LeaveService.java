package com.team2.laps.service;

import java.util.List;

import com.team2.laps.model.Leave;
import com.team2.laps.model.TimePeriod;
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

    public List<Leave> getLeaveByUser(TimePeriod timePeriod, String leaveId) {
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();

        // if (user.getRoles().contains(new Role(RoleName.ROLE_MANAGER)))
        // if (timePeriod == TimePeriod.FROM_NOW_ON) {
        // logger.error("FROM NOW ON");
        // return
        // leaveRepository.findBySubordinatesAfterNowOrderByStartDate(user.getId());
        // }
        // else if (timePeriod == TimePeriod.LEAVE_PERIOD) {
        // Leave leave = leaveRepository.findById(leaveId).get();
        // return
        // leaveRepository.findBySubordinatesWithTimePeriodOrderByStartDate(leave.getStartDate(),
        // leave.getEndDate());
        // } else if (timePeriod == TimePeriod.HISTORY) {
        // return leaveRepository.findAllBySubordinates();
        // }
        return leaveRepository.findByUserOrderByStartDate(user.getId());
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
