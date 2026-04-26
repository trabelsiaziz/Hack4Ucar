// ─────────────────────────────────────────────────────────────────────────────
// KPI Dashboard API — DTO Types
// Source: BACKEND/BACK/docs/kpi-dashboard-api.md
// Base URL: http://localhost:8080/api/dashboard
// ─────────────────────────────────────────────────────────────────────────────

// ── Shared ────────────────────────────────────────────────────────────────────

export type TrendDirection = "up" | "down" | "stable";

export type ApiChartType = "line" | "bar" | "stacked-bar" | "donut";

export type AlertSeverity = "low" | "medium" | "high" | "critical";
export type AlertStatus = "open" | "acknowledged" | "closed";
export type AlertType = "ACADEMIC" | "FINANCE" | "HR";

export type RecommendationPriority = "low" | "medium" | "high" | "urgent";
export type RecommendationStatus =
  | "proposed"
  | "accepted"
  | "rejected"
  | "completed";
export type RecommendationCategory = "ACADEMIC" | "FINANCE" | "HR";

// ── StatCardDto ───────────────────────────────────────────────────────────────

export interface StatCardScope {
  scopeType: "institution" | "global";
  scopeId: string;
}

export interface StatCardDto {
  widgetType: "stat-card";
  title: string;
  value: number;
  unit: string;
  trend: TrendDirection;
  delta: number;
  periodId: string;
  scope: StatCardScope;
}

// ── ChartDto ──────────────────────────────────────────────────────────────────

export interface ChartSeriesDto {
  key: string;
  label: string;
  data: number[];
  unit: string;
}

export interface ChartDto {
  widgetType: "chart";
  chartType: ApiChartType;
  title: string;
  /** Present for line charts — x-axis period labels */
  xAxis: string[] | null;
  /** Present for bar / stacked-bar — category labels */
  categories: string[] | null;
  series: ChartSeriesDto[];
}

// ── /overview — OverviewDto ───────────────────────────────────────────────────

export interface OverviewDto {
  kpiCards: StatCardDto[];
  openAlertsCount: number;
  recommendationsCount: number;
}

// ── /finance — FinanceDashboardDto ────────────────────────────────────────────

export interface FinanceDashboardDto {
  allocatedVsConsumedChart: ChartDto;
  spendingByDepartmentChart: ChartDto;
}

// ── /hr — HrDashboardDto ──────────────────────────────────────────────────────

export interface HrDashboardDto {
  headcountChart: ChartDto;
  teachingLoadChart: ChartDto;
  absenteeismChart: ChartDto;
}

// ── /alerts — AlertsDashboardResponse ─────────────────────────────────────────

export interface AlertItemDto {
  alertId: string;
  severity: AlertSeverity;
  alertType: AlertType;
  message: string;
  institutionId: string;
  periodId: string;
  status: AlertStatus;
  observedValue: number;
  thresholdValue: number;
}

export interface AlertListDto {
  widgetType: "alert-list";
  title: string;
  items: AlertItemDto[];
}

export interface AlertSummaryDto {
  totalOpen: number;
  bySeverity: {
    critical: number;
    high: number;
    medium: number;
    low: number;
  };
}

export interface AlertsDashboardResponse {
  alertList: AlertListDto;
  summary: AlertSummaryDto;
}

// ── /insights — InsightsDashboardDto ──────────────────────────────────────────

export interface ExecutiveSummaryDto {
  widgetType: "insight-card";
  title: string;
  summaryText: string;
  confidenceScore: number;
  insightType: string;
}

export interface RecommendationItemDto {
  recommendationId: string;
  priority: RecommendationPriority;
  category: RecommendationCategory;
  title: string;
  description: string;
  status: RecommendationStatus;
}

export interface TopRecommendationsDto {
  widgetType: "recommendation-list";
  title: string;
  items: RecommendationItemDto[];
}

export interface InsightsDashboardDto {
  executiveSummary: ExecutiveSummaryDto;
  topRecommendations: TopRecommendationsDto;
}

// ── Query param bags ──────────────────────────────────────────────────────────

export interface OverviewParams {
  institutionId: string;
  institutionCode: string;
  periodId: string;
  periodLabel: string;
}

export interface TrendsParams {
  kpi: "successRate" | "attendanceRate" | "budgetExecutionRate" | "absenteeismRate";
  institutionId: string;
  periodIds: string;   // comma-separated UUIDs
  periodLabels: string; // comma-separated labels
}

export interface ComparisonParams {
  kpi:
    | "successRate"
    | "attendanceRate"
    | "budgetExecutionRate"
    | "absenteeismRate"
    | "costPerStudent";
  institutionIds: string;
  institutionCodes: string;
  periodId: string;
}

export interface FinanceParams {
  institutionIds: string;
  institutionCodes: string;
  periodId: string;
}

export interface HrParams {
  institutionIds: string;
  institutionCodes: string;
  periodId: string;
}

export interface AlertsParams {
  institutionId?: string;
}

export interface InsightsParams {
  institutionId: string;
  periodId: string;
}
