package com.example.springbackendtemplate1.commons.utils;

public interface LocaleSortable {
    /**
     * Return join info when the current sortBy is a locale field, null otherwise.
     * Implementations read getSortBy() / getSortDir() from PaginatedRequest.
     */
    LocaleJoinSortInfo getLocaleSortInfo();
}
