package com.LinkVerse.identity.validator.PhoneValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<PhoneConstraint, String> {
    @Override
    public void initialize(PhoneConstraint phoneNumberNo) {}

    @Override
    public boolean isValid(String phoneNo, ConstraintValidatorContext cxt) {
        if (phoneNo == null || phoneNo.trim().isEmpty()) {
            return true;
        }
        // ✅ Kiểm tra xem phoneNo chỉ chứa 10 chữ số (không có ký tự đặc biệt)
        boolean isValid = phoneNo.matches("^[0-9]{10}$");

        if (!isValid) {
            System.out.println("⚠ Phone number validation failed: " + phoneNo);
        }

        return isValid;
    }
}
