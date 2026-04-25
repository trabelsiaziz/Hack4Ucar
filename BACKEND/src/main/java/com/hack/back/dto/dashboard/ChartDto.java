package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Generic chart widget response — line / bar / stacked-bar / donut.
 * Matches the shape: { widgetType, chartType, title, xAxis/categories, series }
 */
@Data
@Builder
public class ChartDto {

    private final String widgetType = "chart";

    /** line | bar | stacked-bar | donut */
    private String chartType;
    private String title;

    /** x-axis labels for time-series charts */
    private List<String> xAxis;

    /** category labels for comparison/stacked charts */
    private List<String> categories;

    private List<SeriesDto> series;

    @Data
    @Builder
    public static class SeriesDto {
        private String key;
        private String label;
        private List<Double> data;
        private String unit;
    }
}
