package com.team2.laps.service;

import java.time.Duration;
import java.util.List;

import com.team2.laps.model.Leave;
import com.team2.laps.model.LeaveStatus;
import com.team2.laps.model.LeaveType;
import com.team2.laps.model.User;
import com.team2.laps.repository.LeaveRepository;
import com.team2.laps.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);

    @Autowired
    LeaveRepository leaveRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    public List<Leave> getLeaveByUser(boolean isManager) {
        // Get current logged in user
        User user = userService.getCurrentUser();
        if (isManager)
            return leaveRepository.findLeaveForApprovalBySubordinatesOrderByStartDate(user.getId());
        else
            return leaveRepository.findCurrentYearLeaveByUserOrderByStartDate(user.getId());
    }

    public boolean createOrUpdateLeave(Leave leave, boolean isManager) {
        if (isValid(leave, isManager)) {
            if (leaveRepository.save(leave) != null)
                return true;
            else
                return false;
        }
        return false;
    }

    public void deleteLeave(String leaveId) {
        logger.error(leaveId);
        leaveRepository.deleteById(leaveId);
    }

    public boolean isValid(Leave leave, boolean isManager) {
        // Validate claim date
        if (leave.getStartDate().compareTo(leave.getEndDate()) >= 0) {
            logger.error("invalid date");

            return false;
        }
        // Validate left time for leave
        if (leave.getLeaveType() == LeaveType.ANNUAL) {
            Duration duration = Duration.between(leave.getStartDate(), leave.getEndDate());
            if (duration.toDays() > leave.getUser().getAnnualLeaveLeft()) {
                logger.error("no enough time left");

                return false;
            }
        } else if (leave.getLeaveType() == LeaveType.MEDICAL) {
            Duration duration = Duration.between(leave.getStartDate(), leave.getEndDate());
            if (duration.toDays() > leave.getUser().getMedicalLeaveLeft()) {
                logger.error("no enough time left");

                return false;
            }
        }
        // Validate rejected reason
        if (leave.getStatus() == LeaveStatus.REJECTED && leave.getRejectReason() == null)
            return false;
        // Validate status change
        if (leave.getId() != null) {
            boolean isStatusChangeValid = false;
            Leave oldLeave = leaveRepository.findById(leave.getId()).get();
            if (isManager) {
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && (leave.getStatus() == LeaveStatus.APPROVED || leave.getStatus() == LeaveStatus.REJECTED))
                    isStatusChangeValid = true;
                else
                    isStatusChangeValid = false;
            } else {
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && leave.getStatus() == LeaveStatus.DELETED)
                    isStatusChangeValid = true;
                else if (oldLeave.getStatus() == LeaveStatus.APPLIED && leave.getStatus() == LeaveStatus.UPDATED)
                    isStatusChangeValid = true;
                else if (oldLeave.getStatus() == LeaveStatus.APPROVED && leave.getStatus() == LeaveStatus.CANCELED)
                    isStatusChangeValid = true;
                else
                    isStatusChangeValid = false;
            }
            if (!isStatusChangeValid)
                return false;
        }
        return true;
    }
}
