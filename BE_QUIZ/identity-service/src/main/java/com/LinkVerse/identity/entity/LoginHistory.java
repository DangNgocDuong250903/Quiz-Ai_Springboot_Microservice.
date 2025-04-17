package com.LinkVerse.identity.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "login_history")
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "login_time", nullable = false)
    LocalDateTime loginTime;

    @Embedded
    DeviceInfo deviceInfo;

    @PrePersist
    protected void onCreate() {
        loginTime = LocalDateTime.now();
    }
}
