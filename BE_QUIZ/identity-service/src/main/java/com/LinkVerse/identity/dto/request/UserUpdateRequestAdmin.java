package com.LinkVerse.identity.dto.request;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.LinkVerse.identity.validator.DobValidator.DobConstraint;
import com.LinkVerse.identity.validator.PhoneValidator.PhoneConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequestAdmin {
    String password;
    String firstName;
    String lastName;

    @PhoneConstraint(message = "Phone number invalid format")
    String phoneNumber = "";

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    String email;

    @DobConstraint(min = 18, message = "Date of birth invalid format")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    Date dateOfBirth;

    List<String> roles;

    String city;
    String mssv;
}
