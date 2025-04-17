package com.LinkVerse.identity.dto.request;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.LinkVerse.identity.entity.Gender;
import com.LinkVerse.identity.entity.UserStatus;
import com.LinkVerse.identity.validator.DobValidator.DobConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreationRequest {
    String userId;

    @Size(min = 4, max = 20, message = "USERNAME_INVALID")
    String username;

    @Size(min = 6, max = 50, message = "INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;

    //    @PhoneConstraint(message = "Phone number invalid format")
    @Size(min = 10, max = 10, message = "Phone number invalid format")
    String phoneNumber = "";

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    String email;

    UserStatus status = UserStatus.ONLINE;

    @DobConstraint(min = 18, message = "Date of birth invalid format")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    Date dateOfBirth;

    String city;
    Gender gender; /**/

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
