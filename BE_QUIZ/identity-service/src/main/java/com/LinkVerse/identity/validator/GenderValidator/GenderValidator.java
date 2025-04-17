package com.LinkVerse.identity.validator.GenderValidator;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.LinkVerse.identity.entity.Gender;

public class GenderValidator implements ConstraintValidator<GenderConstraint, Gender> {
    private Gender[] genders;

    @Override
    public void initialize(GenderConstraint constraint) {
        this.genders = constraint.anyOf();
    }

    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(genders).contains(value);
    }
}
