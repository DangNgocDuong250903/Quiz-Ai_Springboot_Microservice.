package com.LinkVerse.notification.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.LinkVerse.notification.entity.User;

@Repository
public interface AuthenticationRepository {
    Optional<User> findUserByEmail(String email);

    String generatePasswordResetToken(User user);
}
