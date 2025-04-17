package com.LinkVerse.identity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {
    Long id;
    String content;
    List<AnswerResponse> answers;
}
