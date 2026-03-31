package com.example.resortapplication1.commons.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

@Data
public class PaginatedRequest {

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(50)
    private int size = 10;

    private String sortBy = "id";

    private Sort.Direction sortDir = Sort.Direction.ASC;

    public Pageable toPageable(Set<String> allowedSortFields) {
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }
        return PageRequest.of(page, size, Sort.by(sortDir, sortBy));
    }
}