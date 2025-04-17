package com.LinkVerse.identity.dto.response;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import com.LinkVerse.identity.entity.Gender;
import com.LinkVerse.identity.entity.UserStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id; // users id
    String username;
    String firstName;
    String lastName;
    UserStatus status = UserStatus.ONLINE;
    String phoneNumber;
    String email;
    Date dateOfBirth;
    String profileId;
    boolean emailVerified;
    String bio;
    Set<RoleResponse> roles;
    LocalDateTime createdAt;
    String imageUrl;
    Gender gender;
    String city;
    String mssv;
}
