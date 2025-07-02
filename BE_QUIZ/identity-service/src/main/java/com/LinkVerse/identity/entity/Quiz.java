package com.LinkVerse.identity.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title; // Ví dụ: "Đề thi Sử 1", "Đề thi Lý nâng cao"

    @ManyToOne
    Subject subject;

    @Column(name = "duration_minutes")
    Integer durationMinutes; // 30, 60 phút...

    @Column(name = "has_no_time_limit")
    Boolean hasNoTimeLimit = false;

    @Column(name = "expiration_time")
    LocalDateTime expirationTime; // Ngày hết hạn truy cập đề thi (có thể null)

   @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
   @JsonManagedReference
   Set<Question> questions; // đổi từ List -> Set

}
