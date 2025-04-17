package com.LinkVerse.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.LinkVerse.identity.dto.request.UserCreationRequest;
import com.LinkVerse.identity.dto.request.UserUpdateRequestAdmin;
import com.LinkVerse.identity.dto.response.UserResponse;
import com.LinkVerse.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "gender", source = "gender")
        @Mapping(target = "mssv", source = "mssv")
    User toUser(UserCreationRequest request);

    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "imageUrl", target = "imageUrl")
        @Mapping(target = "mssv", source = "mssv")
    UserResponse toUserResponse(User user);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "roles", ignore = true)
            @Mapping(target = "mssv", source = "mssv")
    void updateUser(@MappingTarget User user, UserUpdateRequestAdmin request);
}
