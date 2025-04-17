package com.LinkVerse.notification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.LinkVerse.notification.entity.OtpRequest;
import com.LinkVerse.notification.entity.User;

public interface OtpRequestRepository extends JpaRepository<OtpRequest, Long> {
    Optional<OtpRequest> findByUser(User user);
}
