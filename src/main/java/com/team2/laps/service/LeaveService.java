package com.team2.laps.service;

import java.time.Duration;
import java.util.List;

import javax.transaction.Transactional;

import com.team2.laps.model.Leave;
import com.team2.laps.model.LeaveStatus;
import com.team2.laps.model.LeaveType;
import com.team2.laps.model.User;
import com.team2.laps.payload.ApiResponse;
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

    @Transactional
    public List<Leave> getLeaveByUser(boolean isManager) {
        User user = userService.getCurrentUser();
        // Manager return subordinates leave record for approval
        if (isManager)
            return leaveRepository.findLeaveForApprovalBySubordinatesOrderByStartDate(user.getId());
        // Staff return his/her current year record
        else
            return leaveRepository.findCurrentYearLeaveByUserOrderByStartDate(user.getId());
    }

    @Transactional
    public ApiResponse createOrUpdateLeave(Leave leave, boolean isManager) {
        String isValid = isValid(leave, isManager);
        if (isValid == "valid") {
            if (leaveRepository.findById(leave.getId()).isPresent()) {
                Leave oldLeave = leaveRepository.findById(leave.getId()).get();
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && leave.getStatus() == LeaveStatus.APPROVED) {
                    Duration duration = Duration.between(leave.getStartDate(), leave.getEndDate());

                    if (leave.getLeaveType() == LeaveType.ANNUAL) {
                        leave.getUser().setAnnualLeaveLeft(oldLeave.getUser().getAnnualLeaveLeft() - duration.toDays());
                    }
                    if (leave.getLeaveType() == LeaveType.MEDICAL) {
                        leave.getUser()
                                .setMedicalLeaveLeft(oldLeave.getUser().getMedicalLeaveLeft() - duration.toDays());
                    }
                }
            }
            if (leaveRepository.save(leave) != null)
                return new ApiResponse(true, "Leave created or updated");
            else
                return new ApiResponse(false, "Leave create or update failed");
        }
        return new ApiResponse(false, isValid);
    }

    @Transactional
    public ApiResponse deleteOrCancelLeave(String leaveId, LeaveStatus leaveStatus, boolean isManager) {
        if (leaveRepository.findById(leaveId).isPresent()) {
            Leave leave = leaveRepository.findById(leaveId).get();
            if (isValidStatusChange(leave, isManager)) {
                leave.setStatus(leaveStatus);
                if (leaveRepository.save(leave) != null)
                    return new ApiResponse(true, "Leave status changed");
                else
                    return new ApiResponse(false, "Leave status change failed");
            } else {
                return new ApiResponse(false, "Invalid status change");
            }
        } else
            return new ApiResponse(false, "Cannot find leave to be deleted");
    }

    @Transactional
    public String isValid(Leave leave, boolean isManager) {
        // Validate claim date
        if (leave.getStartDate().compareTo(leave.getEndDate()) >= 0) {
            return "invalid date";
        }
        // Validate left time for leave
        if (leave.getLeaveType() == LeaveType.ANNUAL) {
            Duration duration = Duration.between(leave.getStartDate(), leave.getEndDate());
            if (duration.toDays() > leave.getUser().getAnnualLeaveLeft()) {
                return "not enough leave left";
            }
        } else if (leave.getLeaveType() == LeaveType.MEDICAL) {
            Duration duration = Duration.between(leave.getStartDate(), leave.getEndDate());
            if (duration.toDays() > leave.getUser().getMedicalLeaveLeft()) {
                return "not enough leave left";
            }
        }
        if (!isValidStatusChange(leave, isManager))
            return "invalid status change";
        // Validate rejected reason
        if (leave.getStatus() == LeaveStatus.REJECTED && leave.getRejectReason() == null)
            return "need rejected reason";
        return "valid";
    }

    @Transactional
    public boolean isValidStatusChange(Leave leave, boolean isManager) {
        // Validate status change
        LeaveStatus leaveStatus = leave.getStatus();
        if (leave.getId() != null) {
            boolean isStatusChangeValid = false;
            Leave oldLeave = leaveRepository.findById(leave.getId()).get();
            if (oldLeave.getStatus() == leaveStatus)
                return true;
            if (isManager) {
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && (leaveStatus == LeaveStatus.APPROVED || leaveStatus == LeaveStatus.REJECTED))
                    isStatusChangeValid = true;
                else
                    isStatusChangeValid = false;
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && leaveStatus == LeaveStatus.APPROVED) {
                    Duration duration = Duration.between(leave.getStartDate(), leave.getEndDate());

                    if (leave.getLeaveType() == LeaveType.ANNUAL) {
                        isStatusChangeValid = oldLeave.getUser().getAnnualLeaveLeft() > duration.toDays();
                    }
                    if (leave.getLeaveType() == LeaveType.MEDICAL) {
                        isStatusChangeValid = oldLeave.getUser().getMedicalLeaveLeft() > duration.toDays();
                    }
                }
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
        } else if (leaveStatus == LeaveStatus.APPLIED)
            return true;
        else
            return false;
    }
}
