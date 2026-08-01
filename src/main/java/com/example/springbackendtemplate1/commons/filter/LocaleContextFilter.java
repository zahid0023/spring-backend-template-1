package com.example.springbackendtemplate1.commons.filter;

import com.example.springbackendtemplate1.commons.context.LocaleContext;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LocaleContextFilter extends OncePerRequestFilter {

    private final LocaleService localeService;

    public LocaleContextFilter(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            writeMissingHeaderError(request, response);
            return;
        }
        try {
            LocaleContext.setLocaleId(localeService.resolveLocaleId(acceptLanguage));
            filterChain.doFilter(request, response);
        } finally {
            LocaleContext.clear();
        }
    }

    private void writeMissingHeaderError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String requestId = request.getHeader("X-Request-Id");
        String requestIdJson = requestId == null ? "null" : "\"" + requestId.replace("\"", "\\\"") + "\"";

        response.getWriter().write(
                "{\"request_id\":" + requestIdJson + ","
                        + "\"status\":400,"
                        + "\"error\":\"INVALID_ARGUMENT\","
                        + "\"message\":\"Required request header 'Accept-Language' is not present\"}"
        );
    }
}
