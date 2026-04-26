// ─────────────────────────────────────────────────────────────────────────────
// DTO Adapters
// Converts backend ChartDto → the { data[], config: ChartSchema } shape
// that the existing generic ChartRenderer / ChartComponent pipeline expects.
// The existing chart components are NOT modified.
// ─────────────────────────────────────────────────────────────────────────────

import type { ChartDto, ApiChartType } from "@/types";
import type { ChartSchema, ChartType } from "@/types";

// ── chartType mapping ─────────────────────────────────────────────────────────

const API_TYPE_TO_CHART_TYPE: Record<ApiChartType, ChartType> = {
  line: "line",
  bar: "bar",
  "stacked-bar": "stacked-bar",
  donut: "donut",
};

// ── Main adapter ──────────────────────────────────────────────────────────────

/**
 * Converts a `ChartDto` from the API into the flat-row data array + ChartSchema
 * that Recharts / ChartRenderer expects.
 *
 * Strategy:
 * - Build one row per x-axis label (xAxis for line, categories for bar/stacked).
 * - Each row has a `label` key (the x value) plus one key per series.
 * - ChartSchema.xKey = "label", ChartSchema.yKeys = series[].key
 */
export function adaptChartDto(dto: ChartDto): {
  data: Record<string, unknown>[];
  config: ChartSchema;
} {
  const labels = dto.xAxis ?? dto.categories ?? [];

  // Build flat rows: [{ label: "ENIT", successRate: 78.4, ... }, ...]
  const data: Record<string, unknown>[] = labels.map((lbl, i) => {
    const row: Record<string, unknown> = { label: lbl };
    for (const series of dto.series) {
      row[series.key] = series.data[i] ?? null;
    }
    return row;
  });

  const chartType = API_TYPE_TO_CHART_TYPE[dto.chartType] ?? "bar";
  const isStacked = dto.chartType === "stacked-bar";

  const config: ChartSchema = {
    type: chartType,
    xKey: "label",
    yKeys: dto.series.map((s) => s.key),
    stacked: isStacked,
    showLegend: dto.series.length > 1,
    showGrid: true,
    height: 300,
  };

  return { data, config };
}

/**
 * Converts the `bySeverity` map from AlertSummaryDto into the flat
 * [{ name, value }] array that DonutChartComponent expects.
 */
export function adaptSeverityDonut(bySeverity: {
  critical: number;
  high: number;
  medium: number;
  low: number;
}): { name: string; value: number }[] {
  return [
    { name: "Critical", value: bySeverity.critical },
    { name: "High", value: bySeverity.high },
    { name: "Medium", value: bySeverity.medium },
    { name: "Low", value: bySeverity.low },
  ].filter((s) => s.value > 0);
}

/** ChartSchema for the severity donut */
export const SEVERITY_DONUT_CONFIG: ChartSchema = {
  type: "donut",
  dataKey: "value",
  categoryKey: "name",
  showLegend: true,
  colors: [
    "hsl(0 84% 60%)",   // critical — red
    "hsl(25 95% 53%)",  // high — orange
    "hsl(48 96% 53%)",  // medium — yellow
    "hsl(142 71% 45%)", // low — green
  ],
  height: 260,
};
