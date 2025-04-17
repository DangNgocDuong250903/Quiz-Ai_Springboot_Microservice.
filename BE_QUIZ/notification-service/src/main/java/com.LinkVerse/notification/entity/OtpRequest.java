package com.LinkVerse.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private int attempts;
    private LocalDateTime lastRequestTime;

    public OtpRequest(User user, int attempts, LocalDateTime lastRequestTime) {
        this.user = user;
        this.attempts = attempts;
        this.lastRequestTime = lastRequestTime;
    }
}
