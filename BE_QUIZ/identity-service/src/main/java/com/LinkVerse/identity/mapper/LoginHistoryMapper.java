package com.LinkVerse.identity.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.LinkVerse.identity.dto.request.LoginHistoryRequest;
import com.LinkVerse.identity.entity.LoginHistory;

@Mapper(componentModel = "spring")
public interface LoginHistoryMapper {
    LoginHistoryRequest toDto(LoginHistory entity);

    List<LoginHistoryRequest> toDto(List<LoginHistory> entities);
}
