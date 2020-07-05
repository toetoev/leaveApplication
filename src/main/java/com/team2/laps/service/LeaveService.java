package com.team2.laps.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.transaction.Transactional;

import com.team2.laps.model.Leave;
import com.team2.laps.model.LeaveStatus;
import com.team2.laps.model.LeaveType;
import com.team2.laps.model.User;
import com.team2.laps.payload.ApiResponse;
import com.team2.laps.repository.LeaveRepository;
import com.team2.laps.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveService {
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
            return ApiResponse.builder()
                    .data(leaveRepository.findLeaveForApprovalBySubordinatesOrderByStartDate(user.getId())).build();
        // Staff return his/her current year record
        else
            return ApiResponse.builder().data(leaveRepository.findCurrentYearLeaveByUserOrderByStartDate(user.getId()))
                    .build();
    }

    @Transactional
    public ApiResponse createOrUpdateLeave(Leave leave, boolean isManager) {
        String isValid = isValid(leave, isManager);
        if (isValid == "valid") {
            if (leave.getId() != null && leaveRepository.findById(leave.getId()).isPresent()) {
                Leave oldLeave = leaveRepository.findById(leave.getId()).get();
                leave.setUser(oldLeave.getUser());
                User user = oldLeave.getUser();
                if (enoughLeaveLeft(leave)) {
                    long period = calculateAnnualLeaveDuration(leave.getStartDate(), leave.getEndDate());
                    if (leave.getLeaveType() == LeaveType.ANNUAL) {
                        user.setAnnualLeaveLeft(oldLeave.getUser().getAnnualLeaveLeft() - period);
                    }
                    if (leave.getLeaveType() == LeaveType.MEDICAL) {
                        user.setMedicalLeaveLeft(oldLeave.getUser().getMedicalLeaveLeft() - period);
                    }
                } else {
                    return ApiResponse.builder().success(false).message("Not enough leave left").build();
                }
                leave.setUser(user);
            }
            if (leaveRepository.save(leave) != null)
                return ApiResponse.builder().success(true).message("Leave created or updated").build();
            else
                return ApiResponse.builder().success(false).message("Leave create or update failed").build();
        }
        return ApiResponse.builder().success(false).message(isValid).build();
    }

    @Transactional
    public ApiResponse deleteOrCancelLeave(String leaveId, LeaveStatus leaveStatus, boolean isManager) {
        if (leaveRepository.findById(leaveId).isPresent()) {
            Leave leave = leaveRepository.findById(leaveId).get();
            if (isValidStatusChange(leave, isManager) == "valid status change") {
                leave.setStatus(leaveStatus);
                if (leaveRepository.save(leave) != null)
                    return ApiResponse.builder().success(true).message("Leave status changed").build();
                else
                    return ApiResponse.builder().success(false).message("Leave status change failed").build();
            } else {
                return ApiResponse.builder().success(false).message("Invalid status change").build();
            }
        } else
            return ApiResponse.builder().success(false).message("Cannot find leave to be deleted").build();
    }

    @Transactional
    public String isValid(Leave leave, boolean isManager) {
        // Validate claim date
        if (leave.getStartDate().compareTo(leave.getEndDate()) >= 0) {
            return "Invalid date";
        }
        // Validate status change
        String isValidStatusChange = isValidStatusChange(leave, isManager);
        if (isValidStatusChange != "valid status change")
            return isValidStatusChange;
        // Validate rejected reason
        if (leave.getStatus() == LeaveStatus.REJECTED && leave.getRejectReason() == null)
            return "Need rejected reason";
        return "valid";
    }

    @Transactional
    public String isValidStatusChange(Leave leave, boolean isManager) {
        // Validate status change
        LeaveStatus leaveStatus = leave.getStatus();
        if (leave.getId() == null && leaveStatus == LeaveStatus.APPLIED) {
            if (enoughLeaveLeft(leave))
                return "valid status change";
            else
                return "Not enough leave left";
        }
        if (leave.getId() != null && leaveRepository.findById(leave.getId()).isPresent()) {
            boolean isStatusChangeValid = false;
            Leave oldLeave = leaveRepository.findById(leave.getId()).get();
            if (oldLeave.getStatus() == leaveStatus)
                return "valid status change";
            if (isManager) {
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && (leaveStatus == LeaveStatus.APPROVED || leaveStatus == LeaveStatus.REJECTED))
                    isStatusChangeValid = true;
                else
                    isStatusChangeValid = false;
                if ((oldLeave.getStatus() == LeaveStatus.APPLIED || oldLeave.getStatus() == LeaveStatus.UPDATED)
                        && leaveStatus == LeaveStatus.APPROVED) {
                    long period = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());

                    if (leave.getLeaveType() == LeaveType.ANNUAL) {
                        isStatusChangeValid = oldLeave.getUser().getAnnualLeaveLeft() >= period;
                    }
                    if (leave.getLeaveType() == LeaveType.MEDICAL) {
                        isStatusChangeValid = oldLeave.getUser().getMedicalLeaveLeft() >= period;
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
            if (isStatusChangeValid)
                return "valid status change";
            else
                return "Invalid status change";
        } else
            return "Invalid status change";
    }

    public long calculateAnnualLeaveDuration(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        long result = 0;
        if (days <= 14) {
            result = (days / 7) * 5;
            for (long i = 0; i < days % 7; i++) {
                if (startDate.plusDays(i).getDayOfWeek() != DayOfWeek.SATURDAY
                        && startDate.plusDays(i).getDayOfWeek() != DayOfWeek.SUNDAY)
                    result++;
            }
            return result;
        } else
            return days;
    }

    public boolean enoughLeaveLeft(Leave leave) {
        if (leave.getLeaveType() == LeaveType.ANNUAL) {
            long period = calculateAnnualLeaveDuration(leave.getStartDate(), leave.getEndDate());
            if (period > leave.getUser().getAnnualLeaveLeft())
                return false;
            else
                return true;
        } else if (leave.getLeaveType() == LeaveType.MEDICAL) {
            long period = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());
            if (period > leave.getUser().getMedicalLeaveLeft())
                return false;
            else
                return true;
        }
        return false;
    }
}
