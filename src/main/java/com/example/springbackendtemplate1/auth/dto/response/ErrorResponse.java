package com.example.springbackendtemplate1.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private Integer status;             // HTTP status code
    private String error;               // Short error description
    private String message;             // Detailed error message
}
