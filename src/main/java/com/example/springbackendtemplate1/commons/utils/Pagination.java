package com.example.springbackendtemplate1.commons.utils;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

import java.util.Set;

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

    public static <T> PaginatedResponse<T> buildPaginatedResponse(Page<@NonNull T> page,
                                                                   Set<String> sortableFields,
                                                                   Set<String> searchableFields) {
        PaginatedResponse<T> response = buildPaginatedResponse(page);
        response.setSortableFields(sortableFields);
        response.setSearchableFields(searchableFields);
        return response;
    }
}
