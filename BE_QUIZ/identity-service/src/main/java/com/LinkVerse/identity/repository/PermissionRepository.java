package com.LinkVerse.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LinkVerse.identity.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
