package com.LinkVerse.identity.service;

import com.LinkVerse.identity.annotation.RequirePermission;
import com.LinkVerse.identity.entity.User;
import com.LinkVerse.identity.service.PermissionService;
import com.LinkVerse.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionCheckAspect {

    private final PermissionService permissionService;
    private final UserService userService;

    @Before("@annotation(requirePermission)")
    public void checkPermission(RequirePermission requirePermission) {
        String permissionName = requirePermission.value();
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.findUserById(userId);

        boolean hasPermission = permissionService.hasPermission(user, permissionName);
        if (!hasPermission) {
            throw new SecurityException("Bạn không có quyền: " + permissionName);
        }
    }
}
