// ─────────────────────────────────────────────────────────────────────────────
// KPI Dashboard API Client
// Calls http://localhost:8080/api/dashboard/*
// Falls back to rich mock data when the backend is unreachable.
// ─────────────────────────────────────────────────────────────────────────────

import type {
  OverviewDto,
  OverviewParams,
  ChartDto,
  TrendsParams,
  ComparisonParams,
  FinanceDashboardDto,
  FinanceParams,
  HrDashboardDto,
  HrParams,
  AlertsDashboardResponse,
  AlertsParams,
  InsightsDashboardDto,
  InsightsParams,
} from "@/types";

const BASE = "http://localhost:8080/api/dashboard";

// ── helpers ───────────────────────────────────────────────────────────────────

function toQuery(params: Record<string, unknown>): string {
  const q = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== null) q.set(k, String(v));
  }
  return q.toString() ? `?${q.toString()}` : "";
}

async function get<T>(path: string, fallback: T): Promise<T> {
  try {
    const res = await fetch(`${BASE}${path}`, {
      headers: { Accept: "application/json" },
      // 5 s timeout so the page doesn't hang when back is down
      signal: AbortSignal.timeout(5000),
    });
    if (!res.ok) {
      console.warn(`[dashboard-api] ${path} → HTTP ${res.status}, using mock`);
      return fallback;
    }
    return (await res.json()) as T;
  } catch {
    console.warn(`[dashboard-api] ${path} unreachable, using mock data`);
    return fallback;
  }
}

// ── Mock data (used when backend is unreachable) ──────────────────────────────

const MOCK_OVERVIEW: OverviewDto = {
  kpiCards: [
    {
      widgetType: "stat-card",
      title: "Student Success Rate",
      value: 78.4,
      unit: "%",
      trend: "up",
      delta: 2.1,
      periodId: "660e8400-e29b-41d4-a716-446655440001",
      scope: { scopeType: "institution", scopeId: "ENIT" },
    },
    {
      widgetType: "stat-card",
      title: "Attendance Rate",
      value: 91.2,
      unit: "%",
      trend: "stable",
      delta: 0.3,
      periodId: "660e8400-e29b-41d4-a716-446655440001",
      scope: { scopeType: "institution", scopeId: "ENIT" },
    },
    {
      widgetType: "stat-card",
      title: "Budget Execution Rate",
      value: 85.7,
      unit: "%",
      trend: "up",
      delta: 4.5,
      periodId: "660e8400-e29b-41d4-a716-446655440001",
      scope: { scopeType: "institution", scopeId: "ENIT" },
    },
    {
      widgetType: "stat-card",
      title: "Absenteeism Rate",
      value: 6.3,
      unit: "%",
      trend: "down",
      delta: -1.2,
      periodId: "660e8400-e29b-41d4-a716-446655440001",
      scope: { scopeType: "institution", scopeId: "ENIT" },
    },
  ],
  openAlertsCount: 12,
  recommendationsCount: 5,
};

const MOCK_TRENDS: ChartDto = {
  widgetType: "chart",
  chartType: "line",
  title: "Student Success Rate — Trend",
  xAxis: ["2023-S1", "2023-S2", "2024-S1", "2024-S2", "2025-S1"],
  categories: null,
  series: [
    {
      key: "successRate",
      label: "Success Rate",
      data: [70.1, 72.4, 74.8, 76.1, 78.4],
      unit: "%",
    },
  ],
};

const MOCK_COMPARISON: ChartDto = {
  widgetType: "chart",
  chartType: "bar",
  title: "Success Rate — Institution Comparison",
  xAxis: null,
  categories: ["ENIT", "FSEG", "IHEC", "ISET", "ESSTT"],
  series: [
    {
      key: "successRate",
      label: "Success Rate",
      data: [78.4, 65.2, 83.1, 71.5, 69.8],
      unit: "%",
    },
  ],
};

const MOCK_FINANCE: FinanceDashboardDto = {
  allocatedVsConsumedChart: {
    widgetType: "chart",
    chartType: "stacked-bar",
    title: "Allocated vs Consumed Budget",
    xAxis: null,
    categories: ["ENIT", "FSEG", "IHEC", "ISET"],
    series: [
      {
        key: "allocated",
        label: "Allocated Budget",
        data: [500000, 320000, 280000, 210000],
        unit: "TND",
      },
      {
        key: "consumed",
        label: "Consumed Budget",
        data: [428500, 274240, 231000, 178500],
        unit: "TND",
      },
    ],
  },
  spendingByDepartmentChart: {
    widgetType: "chart",
    chartType: "bar",
    title: "Cost per Student",
    xAxis: null,
    categories: ["ENIT", "FSEG", "IHEC", "ISET"],
    series: [
      {
        key: "costPerStudent",
        label: "Cost per Student",
        data: [4285, 3428, 3100, 2950],
        unit: "TND",
      },
    ],
  },
};

const MOCK_HR: HrDashboardDto = {
  headcountChart: {
    widgetType: "chart",
    chartType: "bar",
    title: "Headcount by Type",
    xAxis: null,
    categories: ["ENIT", "FSEG", "IHEC", "ISET"],
    series: [
      {
        key: "teaching",
        label: "Teaching Staff",
        data: [120, 85, 72, 58],
        unit: "persons",
      },
      {
        key: "administrative",
        label: "Administrative Staff",
        data: [45, 30, 28, 22],
        unit: "persons",
      },
    ],
  },
  teachingLoadChart: {
    widgetType: "chart",
    chartType: "bar",
    title: "Average Teaching Load",
    xAxis: null,
    categories: ["ENIT", "FSEG", "IHEC", "ISET"],
    series: [
      {
        key: "teachingLoad",
        label: "Teaching Load",
        data: [18.5, 16.2, 17.0, 15.8],
        unit: "hrs/week",
      },
    ],
  },
  absenteeismChart: {
    widgetType: "chart",
    chartType: "bar",
    title: "Absenteeism Rate",
    xAxis: null,
    categories: ["ENIT", "FSEG", "IHEC", "ISET"],
    series: [
      {
        key: "absenteeismRate",
        label: "Absenteeism Rate",
        data: [6.3, 8.1, 5.9, 7.4],
        unit: "%",
      },
    ],
  },
};

const MOCK_ALERTS: AlertsDashboardResponse = {
  alertList: {
    widgetType: "alert-list",
    title: "Open Alerts",
    items: [
      {
        alertId: "770e8400-0001",
        severity: "critical",
        alertType: "ACADEMIC",
        message: "Student success rate dropped below 70% threshold at ISET.",
        institutionId: "aaa-111",
        periodId: "660e8400-e29b-41d4-a716-446655440001",
        status: "open",
        observedValue: 67.3,
        thresholdValue: 70.0,
      },
      {
        alertId: "770e8400-0002",
        severity: "high",
        alertType: "FINANCE",
        message: "Budget consumption exceeded 90% with 6 weeks remaining.",
        institutionId: "bbb-222",
        periodId: "660e8400-e29b-41d4-a716-446655440001",
        status: "open",
        observedValue: 91.5,
        thresholdValue: 90.0,
      },
      {
        alertId: "770e8400-0003",
        severity: "high",
        alertType: "HR",
        message: "Absenteeism rate above 8% threshold at FSEG.",
        institutionId: "bbb-222",
        periodId: "660e8400-e29b-41d4-a716-446655440001",
        status: "open",
        observedValue: 8.1,
        thresholdValue: 8.0,
      },
      {
        alertId: "770e8400-0004",
        severity: "medium",
        alertType: "ACADEMIC",
        message: "Attendance rate declined 5% vs prior period.",
        institutionId: "ccc-333",
        periodId: "660e8400-e29b-41d4-a716-446655440001",
        status: "open",
        observedValue: 85.0,
        thresholdValue: 90.0,
      },
      {
        alertId: "770e8400-0005",
        severity: "medium",
        alertType: "FINANCE",
        message: "Cost per student increased 12% from last period.",
        institutionId: "aaa-111",
        periodId: "660e8400-e29b-41d4-a716-446655440001",
        status: "open",
        observedValue: 4800,
        thresholdValue: 4500,
      },
      {
        alertId: "770e8400-0006",
        severity: "low",
        alertType: "HR",
        message: "Teaching load slightly above recommended maximum.",
        institutionId: "ccc-333",
        periodId: "660e8400-e29b-41d4-a716-446655440001",
        status: "open",
        observedValue: 19.2,
        thresholdValue: 18.0,
      },
    ],
  },
  summary: {
    totalOpen: 12,
    bySeverity: { critical: 1, high: 3, medium: 5, low: 3 },
  },
};

const MOCK_INSIGHTS: InsightsDashboardDto = {
  executiveSummary: {
    widgetType: "insight-card",
    title: "Executive Summary — 2025-S1",
    summaryText:
      "ENIT shows strong improvement in academic performance with a success rate of 78.4% (+2.1% vs last period). Budget execution is healthy at 85.7%. HR absenteeism decreased to 6.3%, signaling improved workforce stability. Recommend continued investment in tutoring programs to sustain the academic upward trend.",
    confidenceScore: 0.87,
    insightType: "EXECUTIVE_SUMMARY",
  },
  topRecommendations: {
    widgetType: "recommendation-list",
    title: "Top Recommendations",
    items: [
      {
        recommendationId: "990e8400-0001",
        priority: "high",
        category: "ACADEMIC",
        title: "Reinforce tutoring in engineering modules",
        description:
          "Modules with success rates below 60% require targeted tutoring programs before end of semester.",
        status: "proposed",
      },
      {
        recommendationId: "990e8400-0002",
        priority: "high",
        category: "FINANCE",
        title: "Review FSEG discretionary spending",
        description:
          "Budget consumption pace at FSEG exceeds 90% with 6 weeks remaining — review non-essential expenditures.",
        status: "proposed",
      },
      {
        recommendationId: "990e8400-0003",
        priority: "medium",
        category: "HR",
        title: "Launch HR wellness programme at FSEG",
        description:
          "Absenteeism at 8.1% is above the 8% threshold. A structured wellness programme could reduce this within one semester.",
        status: "proposed",
      },
      {
        recommendationId: "990e8400-0004",
        priority: "medium",
        category: "ACADEMIC",
        title: "Address attendance decline at IHEC",
        description:
          "Attendance dropped 5 pp vs prior period. Early-intervention attendance tracking is recommended.",
        status: "proposed",
      },
      {
        recommendationId: "990e8400-0005",
        priority: "low",
        category: "HR",
        title: "Cap teaching load at IHEC",
        description:
          "Average load of 19.2 hrs/week slightly exceeds policy maximum. Redistribution across adjunct staff is advised.",
        status: "proposed",
      },
    ],
  },
};

// ── Default demo params ───────────────────────────────────────────────────────

export const DEMO_PARAMS = {
  institutionId: "550e8400-e29b-41d4-a716-446655440000",
  institutionCode: "ENIT",
  institutionIds: "550e8400-e29b-41d4-a716-446655440000,aaa-111,bbb-222,ccc-333",
  institutionCodes: "ENIT,FSEG,IHEC,ISET",
  periodId: "660e8400-e29b-41d4-a716-446655440001",
  periodLabel: "2025-S1",
  periodIds: "aaa-p1,bbb-p2,ccc-p3,ddd-p4,eee-p5",
  periodLabels: "2023-S1,2023-S2,2024-S1,2024-S2,2025-S1",
} as const;

// ── Public API ────────────────────────────────────────────────────────────────

export const dashboardApi = {
  async fetchOverview(params: OverviewParams): Promise<OverviewDto> {
    return get(`/overview${toQuery(params)}`, MOCK_OVERVIEW);
  },

  async fetchTrends(params: TrendsParams): Promise<ChartDto> {
    return get(`/trends${toQuery(params)}`, MOCK_TRENDS);
  },

  async fetchComparison(params: ComparisonParams): Promise<ChartDto> {
    return get(`/comparison${toQuery(params)}`, MOCK_COMPARISON);
  },

  async fetchFinance(params: FinanceParams): Promise<FinanceDashboardDto> {
    return get(`/finance${toQuery(params)}`, MOCK_FINANCE);
  },

  async fetchHr(params: HrParams): Promise<HrDashboardDto> {
    return get(`/hr${toQuery(params)}`, MOCK_HR);
  },

  async fetchAlerts(params?: AlertsParams): Promise<AlertsDashboardResponse> {
    return get(`/alerts${toQuery(params ?? {})}`, MOCK_ALERTS);
  },

  async fetchInsights(params: InsightsParams): Promise<InsightsDashboardDto> {
    return get(`/insights${toQuery(params)}`, MOCK_INSIGHTS);
  },
};
