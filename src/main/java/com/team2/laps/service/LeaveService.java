package com.team2.laps.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.team2.laps.model.Leave;
import com.team2.laps.model.LeaveStatus;
import com.team2.laps.model.LeaveType;
import com.team2.laps.model.User;
import com.team2.laps.payload.ApiResponse;
import com.team2.laps.repository.LeaveRepository;
import com.team2.laps.repository.UserRepository;
import com.team2.laps.validation.LeaveIDExisting;
import com.team2.laps.validation.OnCreate;
import com.team2.laps.validation.OnUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
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

    public User findUserByLeaveId(String leaveId) {
        return leaveRepository.findById(leaveId).get().getUser();
    }

    @Transactional
    @Validated(OnCreate.class)
    public ApiResponse createLeave(@Valid Leave leave) {
        if (leaveRepository.save(leave) != null)
            return ApiResponse.builder().success(true).message("Leave created successfully").build();
        else
            return ApiResponse.builder().success(false).message("Leave creation failed").build();
    }

    @Transactional
    @Validated(OnUpdate.class)
    public ApiResponse updateLeave(@Valid Leave leave, boolean isManager) {
        Leave oldLeave = leaveRepository.findById(leave.getId()).get();
        leave.setUser(oldLeave.getUser());
        User user = oldLeave.getUser();
        long period = calculateAnnualLeaveDuration(leave.getStartDate(), leave.getEndDate());
        if (leave.getLeaveType() == LeaveType.ANNUAL) {
            user.setAnnualLeaveLeft(oldLeave.getUser().getAnnualLeaveLeft() - period);
        }
        if (leave.getLeaveType() == LeaveType.MEDICAL) {
            user.setMedicalLeaveLeft(oldLeave.getUser().getMedicalLeaveLeft() - period);
        }
        leave.setUser(user);
        if (leaveRepository.save(leave) != null)
            return ApiResponse.builder().success(true).message("Leave created successfully").build();
        else
            return ApiResponse.builder().success(false).message("Leave creation failed").build();
    }

    @Transactional
    @Validated(OnUpdate.class)
    public ApiResponse deleteOrCancelLeave(@Valid @LeaveIDExisting String leaveId, LeaveStatus leaveStatus,
            boolean isManager) {
        Leave leave = leaveRepository.findById(leaveId).get();
        leave.setStatus(leaveStatus);
        if (leaveRepository.save(leave) != null)
            return ApiResponse.builder().success(true).message("Leave status changed").build();
        else
            return ApiResponse.builder().success(false).message("Leave status change failed").build();
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
}
