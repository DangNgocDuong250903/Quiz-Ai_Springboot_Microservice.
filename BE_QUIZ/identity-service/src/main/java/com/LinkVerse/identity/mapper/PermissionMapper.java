package com.LinkVerse.identity.mapper;

import org.mapstruct.Mapper;

import com.LinkVerse.identity.dto.request.PermissionRequest;
import com.LinkVerse.identity.dto.response.PermissionResponse;
import com.LinkVerse.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
