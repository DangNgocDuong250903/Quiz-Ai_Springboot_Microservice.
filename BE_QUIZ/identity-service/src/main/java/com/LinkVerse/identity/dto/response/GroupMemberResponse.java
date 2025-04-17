package com.LinkVerse.identity.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberResponse {
    String userId;
    String username;
    String role;
}
