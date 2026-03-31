package com.example.resortapplication1.utils;

import com.example.resortapplication1.commons.dto.response.PaginatedResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

public class Pagination {
    public static <T> PaginatedResponse<T> buildPaginatedResponse(Page<@NonNull T> page) {
        PaginatedResponse<T> response = new PaginatedResponse<>();
        response.setData(page.getContent());
        response.setCurrentPage(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setPageSize(page.getSize());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
