package com.iqac.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String message;
    private Object data;

    public static ApiResponse of(String message) {
        return new ApiResponse(message, null);
    }

    public static ApiResponse of(String message, Object data) {
        return new ApiResponse(message, data);
    }
}
