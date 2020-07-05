package com.team2.laps.validation;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.team2.laps.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserIDExistingValidator implements ConstraintValidator<UserIDExisting, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        return Objects.isNull(userId) || userRepository.findById(userId).isPresent();
    }
}