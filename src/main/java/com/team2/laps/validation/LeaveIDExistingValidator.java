package com.team2.laps.validation;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.team2.laps.repository.LeaveRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaveIDExistingValidator implements ConstraintValidator<LeaveIDExisting, String> {
    @Autowired
    private LeaveRepository leaveRepository;

    @Override
    public boolean isValid(String leaveId, ConstraintValidatorContext context) {
        return Objects.isNull(leaveId) || leaveRepository.findById(leaveId).isPresent();
    }
}