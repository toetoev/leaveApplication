package com.team2.laps.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.team2.laps.model.LeaveType;
import com.team2.laps.model.User;
import com.team2.laps.repository.LeaveRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AnnualLeaveValidator implements ConstraintValidator<AnnualLeave, User> {
    @Autowired
    LeaveRepository leaveRepository;

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        return user.getAnnualLeaveEntitled() >= leaveRepository.countCurrentYearLeaveUsed(user.getId(),
                LeaveType.ANNUAL.ordinal());
    }
}