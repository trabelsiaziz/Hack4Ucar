"use client";

import useSWR from "swr";
import { dashboardApi, DEMO_PARAMS } from "@/lib/api/dashboard-api";
import type {
  OverviewDto,
  ChartDto,
  FinanceDashboardDto,
  HrDashboardDto,
  AlertsDashboardResponse,
  InsightsDashboardDto,
  TrendsParams,
  ComparisonParams,
} from "@/types";

// ── Overview ──────────────────────────────────────────────────────────────────

export function useOverview(institutionId?: string, periodId?: string) {
  const params = {
    institutionId: institutionId ?? DEMO_PARAMS.institutionId,
    institutionCode: DEMO_PARAMS.institutionCode,
    periodId: periodId ?? DEMO_PARAMS.periodId,
    periodLabel: DEMO_PARAMS.periodLabel,
  };

  const { data, error, isLoading } = useSWR<OverviewDto>(
    ["dashboard/overview", params.institutionId, params.periodId],
    () => dashboardApi.fetchOverview(params),
    { revalidateOnFocus: false, dedupingInterval: 60_000 }
  );

  return { data, isLoading, isError: !!error };
}

// ── Trends ─────────────────────────────────────────────────────────────────────

export function useTrends(
  kpi: TrendsParams["kpi"] = "successRate",
  institutionId?: string
) {
  const params: TrendsParams = {
    kpi,
    institutionId: institutionId ?? DEMO_PARAMS.institutionId,
    periodIds: DEMO_PARAMS.periodIds,
    periodLabels: DEMO_PARAMS.periodLabels,
  };

  const { data, error, isLoading } = useSWR<ChartDto>(
    ["dashboard/trends", kpi, params.institutionId],
    () => dashboardApi.fetchTrends(params),
    { revalidateOnFocus: false, dedupingInterval: 60_000 }
  );

  return { data, isLoading, isError: !!error };
}

// ── Comparison ─────────────────────────────────────────────────────────────────

export function useComparison(
  kpi: ComparisonParams["kpi"] = "successRate",
  periodId?: string
) {
  const params: ComparisonParams = {
    kpi,
    institutionIds: DEMO_PARAMS.institutionIds,
    institutionCodes: DEMO_PARAMS.institutionCodes,
    periodId: periodId ?? DEMO_PARAMS.periodId,
  };

  const { data, error, isLoading } = useSWR<ChartDto>(
    ["dashboard/comparison", kpi, params.periodId],
    () => dashboardApi.fetchComparison(params),
    { revalidateOnFocus: false, dedupingInterval: 60_000 }
  );

  return { data, isLoading, isError: !!error };
}

// ── Finance ─────────────────────────────────────────────────────────────────────

export function useFinance(periodId?: string) {
  const params = {
    institutionIds: DEMO_PARAMS.institutionIds,
    institutionCodes: DEMO_PARAMS.institutionCodes,
    periodId: periodId ?? DEMO_PARAMS.periodId,
  };

  const { data, error, isLoading } = useSWR<FinanceDashboardDto>(
    ["dashboard/finance", params.periodId],
    () => dashboardApi.fetchFinance(params),
    { revalidateOnFocus: false, dedupingInterval: 60_000 }
  );

  return { data, isLoading, isError: !!error };
}

// ── HR ─────────────────────────────────────────────────────────────────────────

export function useHr(periodId?: string) {
  const params = {
    institutionIds: DEMO_PARAMS.institutionIds,
    institutionCodes: DEMO_PARAMS.institutionCodes,
    periodId: periodId ?? DEMO_PARAMS.periodId,
  };

  const { data, error, isLoading } = useSWR<HrDashboardDto>(
    ["dashboard/hr", params.periodId],
    () => dashboardApi.fetchHr(params),
    { revalidateOnFocus: false, dedupingInterval: 60_000 }
  );

  return { data, isLoading, isError: !!error };
}

// ── Alerts ─────────────────────────────────────────────────────────────────────

export function useAlerts(institutionId?: string) {
  const params = institutionId ? { institutionId } : {};

  const { data, error, isLoading } = useSWR<AlertsDashboardResponse>(
    ["dashboard/alerts", institutionId ?? "all"],
    () => dashboardApi.fetchAlerts(params),
    { revalidateOnFocus: false, dedupingInterval: 30_000 }
  );

  return { data, isLoading, isError: !!error };
}

// ── Insights ─────────────────────────────────────────────────────────────────────

export function useInsights(institutionId?: string, periodId?: string) {
  const params = {
    institutionId: institutionId ?? DEMO_PARAMS.institutionId,
    periodId: periodId ?? DEMO_PARAMS.periodId,
  };

  const { data, error, isLoading } = useSWR<InsightsDashboardDto>(
    ["dashboard/insights", params.institutionId, params.periodId],
    () => dashboardApi.fetchInsights(params),
    { revalidateOnFocus: false, dedupingInterval: 60_000 }
  );

  return { data, isLoading, isError: !!error };
}
