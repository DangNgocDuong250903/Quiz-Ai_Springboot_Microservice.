package com.LinkVerse.identity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupResponse {
    String id;
    String name; // The name of the group
    String description;
    int memberCount; // Number of members in the group
    String visibility; // (e.g., PUBLIC, PRIVATE)
}
