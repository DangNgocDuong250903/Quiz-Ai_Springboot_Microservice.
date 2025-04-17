package com.LinkVerse.identity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmojiResponse {
    String symbol;
    String name;
}
