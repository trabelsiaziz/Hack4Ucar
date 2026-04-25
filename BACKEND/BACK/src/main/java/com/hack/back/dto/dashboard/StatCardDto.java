package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Stat-card widget response — one per KPI.
 * Matches the shape: { widgetType, title, value, unit, trend, delta, periodId, scope }
 */
@Data
@Builder
public class StatCardDto {

    private final String widgetType = "stat-card";

    private String title;
    private Double value;
    private String unit;

    /** up | down | stable */
    private String trend;

    /** difference from previous period */
    private Double delta;

    private String periodId;
    private ScopeDto scope;

    @Data
    @Builder
    public static class ScopeDto {
        private String scopeType;
        private String scopeId;
    }
}
