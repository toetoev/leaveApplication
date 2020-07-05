package com.team2.laps.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.team2.laps.model.Leave;
import com.team2.laps.model.LeaveStatus;

import org.springframework.stereotype.Component;

@Component
public class RejectReasonValidator implements ConstraintValidator<RejectReason, Leave> {
    @Override
    public boolean isValid(Leave leave, ConstraintValidatorContext context) {
        if (leave.getStatus() == LeaveStatus.REJECTED && leave.getRejectReason() == null)
            return false;
        return true;
    }
}