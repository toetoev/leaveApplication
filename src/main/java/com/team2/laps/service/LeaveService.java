package com.team2.laps.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;

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
    public ApiResponse getLeaveByUser(boolean isManager) {
        User user = userService.getCurrentUser();
        // Manager return subordinates leave record for approval
        if (isManager)
            return new ApiResponse(leaveRepository.findLeaveForApprovalBySubordinatesOrderByStartDate(user.getId()));
        // Staff return his/her current year record
        else
            return new ApiResponse(leaveRepository.findCurrentYearLeaveByUserOrderByStartDate(user.getId()));
    }

    @Transactional
    public ApiResponse createOrUpdateLeave(Leave leave, boolean isManager) {
        String isValid = isValid(leave, isManager);
        if (isValid == "valid") {
            if (leave.getId() != null && leaveRepository.findById(leave.getId()).isPresent()) {
                Leave oldLeave = leaveRepository.findById(leave.getId()).get();
                leave.setUser(oldLeave.getUser());
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && leave.getStatus() == LeaveStatus.APPROVED) {
                    User user = oldLeave.getUser();
                    if (enoughLeaveLeft(leave)) {
                        Period period = calculateAnnualLeaveDuration(leave.getStartDate(), leave.getEndDate());
                        if (leave.getLeaveType() == LeaveType.ANNUAL) {
                            logger.error(String.valueOf(oldLeave.getUser().getAnnualLeaveLeft()));
                            logger.error(String.valueOf(period.getDays()));
                            user.setAnnualLeaveLeft(oldLeave.getUser().getAnnualLeaveLeft() - period.getDays());
                        }
                        if (leave.getLeaveType() == LeaveType.MEDICAL) {
                            user.setMedicalLeaveLeft(oldLeave.getUser().getMedicalLeaveLeft() - period.getDays());
                        }
                    } else {
                        return new ApiResponse(false, "Not enough leave left");
                    }
                    leave.setUser(user);
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
        // Validate status change
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
        if (leave.getId() == null && leaveStatus == LeaveStatus.APPLIED) {
            if (enoughLeaveLeft(leave))
                return true;
            else
                return false;
        }
        if (leave.getId() != null && leaveRepository.findById(leave.getId()).isPresent()) {
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
                    Period period = Period.between(leave.getStartDate(), leave.getEndDate());

                    if (leave.getLeaveType() == LeaveType.ANNUAL) {
                        isStatusChangeValid = oldLeave.getUser().getAnnualLeaveLeft() >= period.getDays();
                    }
                    if (leave.getLeaveType() == LeaveType.MEDICAL) {
                        isStatusChangeValid = oldLeave.getUser().getMedicalLeaveLeft() >= period.getDays();
                    }
                }
            } else {
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && (leaveStatus == LeaveStatus.DELETED || leaveStatus == LeaveStatus.UPDATED))
                    isStatusChangeValid = true;
                else if (oldLeave.getStatus() == LeaveStatus.APPROVED && leaveStatus == LeaveStatus.CANCELED)
                    isStatusChangeValid = true;
                else
                    isStatusChangeValid = false;
            }
            return isStatusChangeValid;
        } else
            return false;
    }

    public Period calculateAnnualLeaveDuration(LocalDate startDate, LocalDate endDate) {
        int days = Period.between(startDate, endDate).getDays();
        int result = 0;
        if (days <= 14) {
            result = (days / 7) * 5;
            for (int i = 0; i < days % 7; i++) {
                if (startDate.plusDays(i).getDayOfWeek() != DayOfWeek.SATURDAY
                        && startDate.plusDays(i).getDayOfWeek() != DayOfWeek.SUNDAY)
                    result++;
            }
            return Period.ofDays(result);
        } else
            return Period.ofDays(days);
    }

    public boolean enoughLeaveLeft(Leave leave) {
        if (leave.getLeaveType() == LeaveType.ANNUAL) {
            Period period = calculateAnnualLeaveDuration(leave.getStartDate(), leave.getEndDate());
            logger.error(String.valueOf(period.getDays()));
            logger.error(String.valueOf(leave.getUser().getAnnualLeaveLeft()));
            if (period.getDays() > leave.getUser().getAnnualLeaveLeft())
                return false;
            else
                return true;
        } else if (leave.getLeaveType() == LeaveType.MEDICAL) {
            Period period = Period.between(leave.getStartDate(), leave.getEndDate());
            if (period.getDays() > leave.getUser().getMedicalLeaveLeft())
                return false;
            else
                return true;
        }
        return false;
    }
}
