package com.team2.laps.validation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.team2.laps.model.Leave;
import com.team2.laps.model.LeaveType;
import com.team2.laps.model.User;
import com.team2.laps.repository.LeaveRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClaimDateValidator implements ConstraintValidator<ClaimDate, Leave> {
    @Autowired
    LeaveRepository leaveRepository;

    @Override
    public boolean isValid(Leave leave, ConstraintValidatorContext context) {
        if (leave.getStartDate().compareTo(leave.getEndDate()) >= 0)
            return false;
        return enoughLeaveLeft(leave);
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
        User user = leave.getUser();
        if (leave.getId() != null && leaveRepository.findById(leave.getId()).isPresent())
            user = leaveRepository.findById(leave.getId()).get().getUser();
        if (leave.getLeaveType() == LeaveType.ANNUAL) {
            long period = calculateAnnualLeaveDuration(leave.getStartDate(), leave.getEndDate());
            if (period > user.getAnnualLeaveLeft())
                return false;
            else
                return true;
        } else if (leave.getLeaveType() == LeaveType.MEDICAL) {
            long period = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());
            if (period > user.getMedicalLeaveLeft())
                return false;
            else
                return true;
        } else
            return false;
    }
}