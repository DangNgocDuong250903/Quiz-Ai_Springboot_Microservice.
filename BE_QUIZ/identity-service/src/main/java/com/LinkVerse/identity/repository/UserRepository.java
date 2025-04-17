package com.LinkVerse.identity.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LinkVerse.identity.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findById(String requesterId);

    User findUserById(String id);

    User findUserByUsername(String id);

    long countByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    List<User> findUserByDeletedAtBefore(LocalDateTime dateTime);

    void deleteAllByDeletedAtBefore(LocalDateTime dateTime);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    String id(String id);
    boolean existsByMssv(String mssv);

}
