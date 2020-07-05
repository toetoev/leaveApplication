package com.team2.laps.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.team2.laps.model.Leave;
import com.team2.laps.model.LeaveStatus;
import com.team2.laps.model.RoleName;
import com.team2.laps.repository.LeaveRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StatusChangeValidator implements ConstraintValidator<StatusChange, Leave> {
    @Autowired
    private LeaveRepository leaveRepository;

    @Override
    public boolean isValid(Leave leave, ConstraintValidatorContext context) {
        LeaveStatus leaveStatus = leave.getStatus();
        if (leave.getId() == null && leaveStatus == LeaveStatus.APPLIED)
            return true;
        else if (leave.getId() != null && leaveRepository.findById(leave.getId()).isPresent()) {
            boolean isManager = leave.getUser().getRoles().iterator().next().getName() == RoleName.ROLE_MANAGER;
            Leave oldLeave = leaveRepository.findById(leave.getId()).get();
            LeaveStatus oldLeaveStatus = oldLeave.getStatus();
            if (oldLeaveStatus == leaveStatus)
                return true;
            if (isManager) {
                if ((oldLeaveStatus == LeaveStatus.APPLIED || oldLeaveStatus == LeaveStatus.UPDATED)
                        && (leaveStatus == LeaveStatus.APPROVED || leaveStatus == LeaveStatus.REJECTED))
                    return true;
                else
                    return false;
            } else {
                if ((oldLeaveStatus == LeaveStatus.APPLIED || oldLeaveStatus == LeaveStatus.UPDATED)
                        && (leaveStatus == LeaveStatus.DELETED || leaveStatus == LeaveStatus.UPDATED))
                    return true;
                else if (oldLeaveStatus == LeaveStatus.APPROVED && leaveStatus == LeaveStatus.CANCELED)
                    return true;
                else
                    return false;
            }
        }
        return false;
    }
}