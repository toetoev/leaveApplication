package com.team2.laps.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Constraint(validatedBy = LeaveIDExistingValidator.class)
public @interface LeaveIDExisting {
    String message() default "{ProductIDExisting}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}