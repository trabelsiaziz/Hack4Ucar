"use client";

import { useState } from "react";
import { AppShell } from "@/components/shell";
import { AccessGuard } from "@/components/guards";
import { useUserContext } from "@/hooks/useUserContext";
import { useNavigation } from "@/hooks/useNavigation";
import {
  BarChart2,
  TrendingUp,
  GitCompare,
  Wallet,
  Users,
  Bell,
  Lightbulb,
} from "lucide-react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { KpiStatCard, KpiChartWidget, KpiAlertList, KpiInsightCard } from "@/components/widgets/kpi";
import {
  useOverview,
  useTrends,
  useComparison,
  useFinance,
  useHr,
  useAlerts,
  useInsights,
} from "@/hooks/useDashboardData";
import type { TrendsParams, ComparisonParams } from "@/types";

// ── KPI selectors ─────────────────────────────────────────────────────────────

const TREND_KPIS: { value: TrendsParams["kpi"]; label: string }[] = [
  { value: "successRate",         label: "Success Rate" },
  { value: "attendanceRate",      label: "Attendance Rate" },
  { value: "budgetExecutionRate", label: "Budget Execution Rate" },
  { value: "absenteeismRate",     label: "Absenteeism Rate" },
];

const COMPARISON_KPIS: { value: ComparisonParams["kpi"]; label: string }[] = [
  { value: "successRate",         label: "Success Rate" },
  { value: "attendanceRate",      label: "Attendance Rate" },
  { value: "budgetExecutionRate", label: "Budget Execution Rate" },
  { value: "absenteeismRate",     label: "Absenteeism Rate" },
  { value: "costPerStudent",      label: "Cost per Student" },
];

// ── KPI Dashboard inner content ───────────────────────────────────────────────

function KpiDashboardContent() {
  const [trendKpi, setTrendKpi]         = useState<TrendsParams["kpi"]>("successRate");
  const [comparisonKpi, setComparisonKpi] = useState<ComparisonParams["kpi"]>("successRate");

  const overview   = useOverview();
  const trends     = useTrends(trendKpi);
  const comparison = useComparison(comparisonKpi);
  const finance    = useFinance();
  const hr         = useHr();
  const alerts     = useAlerts();
  const insights   = useInsights();

  const alertBadge   = overview.data?.openAlertsCount    ?? 0;
  const insightBadge = overview.data?.recommendationsCount ?? 0;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
          <BarChart2 className="h-5 w-5 text-primary" />
        </div>
        <div>
          <h1 className="text-2xl font-bold tracking-tight">KPI Dashboard</h1>
          <p className="text-sm text-muted-foreground">
            Academic · Finance · HR — powered by real-time data
          </p>
        </div>
      </div>

      {/* Overview stat cards row */}
      <section>
        {overview.isLoading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {[1, 2, 3, 4].map((i) => <Skeleton key={i} className="h-36" />)}
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {(overview.data?.kpiCards ?? []).map((card) => (
              <KpiStatCard key={card.title} card={card} />
            ))}
          </div>
        )}
      </section>

      {/* Tabbed sections */}
      <Tabs defaultValue="trends">
        <TabsList className="flex flex-wrap gap-1 h-auto">
          <TabsTrigger value="trends" className="gap-1.5">
            <TrendingUp className="h-3.5 w-3.5" />
            Trends
          </TabsTrigger>
          <TabsTrigger value="comparison" className="gap-1.5">
            <GitCompare className="h-3.5 w-3.5" />
            Comparison
          </TabsTrigger>
          <TabsTrigger value="finance" className="gap-1.5">
            <Wallet className="h-3.5 w-3.5" />
            Finance
          </TabsTrigger>
          <TabsTrigger value="hr" className="gap-1.5">
            <Users className="h-3.5 w-3.5" />
            HR
          </TabsTrigger>
          <TabsTrigger value="alerts" className="gap-1.5">
            <Bell className="h-3.5 w-3.5" />
            Alerts
            {alertBadge > 0 && (
              <Badge variant="destructive" className="h-4 px-1 text-[10px] ml-0.5">
                {alertBadge}
              </Badge>
            )}
          </TabsTrigger>
          <TabsTrigger value="insights" className="gap-1.5">
            <Lightbulb className="h-3.5 w-3.5" />
            Insights
            {insightBadge > 0 && (
              <Badge variant="secondary" className="h-4 px-1 text-[10px] ml-0.5">
                {insightBadge}
              </Badge>
            )}
          </TabsTrigger>
        </TabsList>

        {/* TRENDS */}
        <TabsContent value="trends" className="mt-4 space-y-4">
          <div className="flex items-center gap-3">
            <p className="text-sm font-medium text-muted-foreground">KPI:</p>
            <Select value={trendKpi} onValueChange={(v) => setTrendKpi(v as TrendsParams["kpi"])}>
              <SelectTrigger className="w-52"><SelectValue /></SelectTrigger>
              <SelectContent>
                {TREND_KPIS.map((k) => (
                  <SelectItem key={k.value} value={k.value}>{k.label}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <KpiChartWidget dto={trends.data} isLoading={trends.isLoading} isError={trends.isError} height={320} />
        </TabsContent>

        {/* COMPARISON */}
        <TabsContent value="comparison" className="mt-4 space-y-4">
          <div className="flex items-center gap-3">
            <p className="text-sm font-medium text-muted-foreground">KPI:</p>
            <Select value={comparisonKpi} onValueChange={(v) => setComparisonKpi(v as ComparisonParams["kpi"])}>
              <SelectTrigger className="w-52"><SelectValue /></SelectTrigger>
              <SelectContent>
                {COMPARISON_KPIS.map((k) => (
                  <SelectItem key={k.value} value={k.value}>{k.label}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <KpiChartWidget dto={comparison.data} isLoading={comparison.isLoading} isError={comparison.isError} height={320} />
        </TabsContent>

        {/* FINANCE */}
        <TabsContent value="finance" className="mt-4">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
            <KpiChartWidget dto={finance.data?.allocatedVsConsumedChart} isLoading={finance.isLoading} isError={finance.isError} />
            <KpiChartWidget dto={finance.data?.spendingByDepartmentChart} isLoading={finance.isLoading} isError={finance.isError} />
          </div>
        </TabsContent>

        {/* HR */}
        <TabsContent value="hr" className="mt-4">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
            <KpiChartWidget dto={hr.data?.headcountChart} isLoading={hr.isLoading} isError={hr.isError} />
            <KpiChartWidget dto={hr.data?.teachingLoadChart} isLoading={hr.isLoading} isError={hr.isError} />
            <div className="lg:col-span-2">
              <KpiChartWidget dto={hr.data?.absenteeismChart} isLoading={hr.isLoading} isError={hr.isError} height={260} />
            </div>
          </div>
        </TabsContent>

        {/* ALERTS */}
        <TabsContent value="alerts" className="mt-4">
          <KpiAlertList data={alerts.data} isLoading={alerts.isLoading} isError={alerts.isError} />
        </TabsContent>

        {/* INSIGHTS */}
        <TabsContent value="insights" className="mt-4">
          <KpiInsightCard data={insights.data} isLoading={insights.isLoading} isError={insights.isError} />
        </TabsContent>
      </Tabs>
    </div>
  );
}

// ── Page wrapper (with AppShell) ──────────────────────────────────────────────

export default function KpiDashboardPage() {
  const { user, isLoading: userLoading } = useUserContext();
  const { navigation, isLoading: navLoading } = useNavigation(user?.roleCode);

  if (userLoading || navLoading) {
    return (
      <div className="flex h-screen">
        <div className="w-64 border-r p-4 space-y-4">
          <Skeleton className="h-8 w-32" />
          {Array.from({ length: 6 }).map((_, i) => (
            <Skeleton key={i} className="h-10 w-full" />
          ))}
        </div>
        <div className="flex-1 p-6 space-y-6">
          <Skeleton className="h-8 w-64" />
          <div className="grid gap-4 md:grid-cols-4">
            {Array.from({ length: 4 }).map((_, i) => (
              <Skeleton key={i} className="h-32" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <AccessGuard user={user} route="/kpi-dashboard" isLoading={false}>
        <div />
      </AccessGuard>
    );
  }

  return (
    <AppShell navigation={navigation} user={user} pageTitle="KPI Dashboard">
      <AccessGuard user={user} route="/kpi-dashboard">
        <KpiDashboardContent />
      </AccessGuard>
    </AppShell>
  );
}
