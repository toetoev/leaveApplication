package com.team2.laps.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.team2.laps.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String nameOrEmail, ConstraintValidatorContext context) {
        return !userRepository.existsByName(nameOrEmail);
    }
}