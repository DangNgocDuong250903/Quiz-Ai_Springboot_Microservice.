package com.LinkVerse.identity.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int currentPage;
    private int pageSize;
    private int totalPage;
    private long totalElement;
    private List<T> data;
}
