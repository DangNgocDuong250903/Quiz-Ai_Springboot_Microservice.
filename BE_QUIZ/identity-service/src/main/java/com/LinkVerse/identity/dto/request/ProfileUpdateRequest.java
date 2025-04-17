package com.LinkVerse.identity.dto.request;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.LinkVerse.identity.entity.Gender;
import com.LinkVerse.identity.entity.UserStatus;
import com.LinkVerse.identity.validator.DobValidator.DobConstraint;
import com.LinkVerse.identity.validator.GenderValidator.GenderConstraint;
import com.LinkVerse.identity.validator.PhoneValidator.PhoneConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUpdateRequest {
    String userId;

    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    String imageUrl;

    String firstName;
    String lastName;

    @GenderConstraint(
            anyOf = {Gender.MALE, Gender.FEMALE, Gender.OTHER},
            message = "INVALID_GENDER")
    Gender gender;

    @PhoneConstraint(message = "Phone number invalid format")
    @Size(min = 10, max = 10, message = "Phone number invalid format")
    String phoneNumber;

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    String email;

    UserStatus status;

    @DobConstraint(min = 18, message = "Date of birth invalid format")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    Date dateOfBirth;

    String city;
}
