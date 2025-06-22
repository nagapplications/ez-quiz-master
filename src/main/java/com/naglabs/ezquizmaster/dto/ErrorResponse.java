package com.naglabs.ezquizmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String code;        // e.g. "SESSION_NOT_FOUND", "LIFELINE_ALREADY_USED"
    private String message;     // Human-readable error
    private Instant timestamp;  // For debugging/logs
    private String requestId; // NEW FIELD
}
