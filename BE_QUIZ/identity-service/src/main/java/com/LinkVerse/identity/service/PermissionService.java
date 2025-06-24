package com.LinkVerse.identity.service;

import com.LinkVerse.identity.dto.request.PermissionRequest;
import com.LinkVerse.identity.dto.response.PermissionResponse;
import com.LinkVerse.identity.entity.Permission;
import com.LinkVerse.identity.entity.User;
import com.LinkVerse.identity.mapper.PermissionMapper;
import com.LinkVerse.identity.repository.PermissionRepository;
import com.LinkVerse.identity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    @Autowired
    UserRepository userRepository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void assignPermissionToUser(String userId, String permissionName) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var permission = permissionRepository.findById(permissionName)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        if (user.getPermissions() == null) {
            user.setPermissions(new HashSet<>());
        }

        user.getPermissions().add(permission);
        userRepository.save(user);
    }

        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
public boolean hasPermission(User user, String permissionName) {
    // Check permission gán trực tiếp
    boolean direct = user.getPermissions() != null &&
                     user.getPermissions().stream().anyMatch(p -> p.getName().equalsIgnoreCase(permissionName));

    // Check permission từ Role
    boolean viaRole = user.getRoles() != null &&
                      user.getRoles().stream()
                          .flatMap(role -> role.getPermissions().stream())
                          .anyMatch(p -> p.getName().equalsIgnoreCase(permissionName));

    return direct || viaRole;
}


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
