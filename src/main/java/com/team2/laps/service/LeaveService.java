package com.team2.laps.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

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

    public boolean deleteLeave(String leaveId, LeaveStatus leaveStatus, boolean isManager) {
        Optional<Leave> leave = leaveRepository.findById(leaveId);
        if (leaveRepository.findById(leaveId).isPresent()) {
            Leave newLeave = leave.get();
            if (isValidStatusChange(leaveId, leaveStatus, isManager)) {
                newLeave.setStatus(leaveStatus);
                if (leaveRepository.save(newLeave) != null)
                    return true;
                else
                    return false;
            } else {
                return false;
            }
        } else {
            logger.error("cannot find");
            return false;

        }
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
        if (!isValidStatusChange(leave.getId(), leave.getStatus(), isManager))
            return false;
        // Validate rejected reason
        if (leave.getStatus() == LeaveStatus.REJECTED && leave.getRejectReason() == null)
            return false;
        return true;
    }

    public boolean isValidStatusChange(String id, LeaveStatus leaveStatus, boolean isManager) {
        // Validate status change
        if (id != null) {
            boolean isStatusChangeValid = false;
            Leave oldLeave = leaveRepository.findById(id).get();
            if (oldLeave.getStatus() == leaveStatus)
                return true;
            if (isManager) {
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && (leaveStatus == LeaveStatus.APPROVED || leaveStatus == LeaveStatus.REJECTED))
                    isStatusChangeValid = true;
                else
                    isStatusChangeValid = false;
            } else {
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && (leaveStatus == LeaveStatus.DELETED || leaveStatus == LeaveStatus.UPDATED))
                    isStatusChangeValid = true;
                else if (oldLeave.getStatus() == LeaveStatus.APPLIED && leaveStatus == LeaveStatus.UPDATED)
                    isStatusChangeValid = true;
                else if (oldLeave.getStatus() == LeaveStatus.APPROVED && leaveStatus == LeaveStatus.CANCELED)
                    isStatusChangeValid = true;
                else
                    isStatusChangeValid = false;
            }
            if (!isStatusChangeValid) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
