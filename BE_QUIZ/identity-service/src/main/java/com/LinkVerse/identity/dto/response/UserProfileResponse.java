package com.LinkVerse.identity.dto.response;

import java.util.Date;

import com.LinkVerse.identity.entity.Gender;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String userId;
    String username;
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
    String imageUrl;
    Date dateOfBirth;
    String city;
    Gender gender;
}
