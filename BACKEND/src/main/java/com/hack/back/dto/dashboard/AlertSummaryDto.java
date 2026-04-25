package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Alert summary: count by severity.
 */
@Data
@Builder
public class AlertSummaryDto {

    private long totalOpen;

    /** e.g. { "high": 3, "medium": 5, "low": 2, "critical": 1 } */
    private Map<String, Long> bySeverity;
}
