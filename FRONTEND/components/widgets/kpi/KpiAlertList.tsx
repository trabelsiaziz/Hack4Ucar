"use client";

import { AlertTriangle, AlertCircle, Info, Shield } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { ChartRenderer } from "@/components/charts";
import { adaptSeverityDonut, SEVERITY_DONUT_CONFIG } from "@/lib/api/dto-adapters";
import { cn } from "@/lib/utils";
import type { AlertsDashboardResponse, AlertSeverity } from "@/types";

interface KpiAlertListProps {
  data: AlertsDashboardResponse | undefined;
  isLoading: boolean;
  isError?: boolean;
}

const severityConfig: Record<
  AlertSeverity,
  { label: string; icon: React.ElementType; rowClass: string; badgeClass: string }
> = {
  critical: {
    label: "Critical",
    icon: Shield,
    rowClass: "border-l-4 border-red-600 bg-red-500/5",
    badgeClass: "bg-red-600/10 text-red-600 border-red-600/20",
  },
  high: {
    label: "High",
    icon: AlertCircle,
    rowClass: "border-l-4 border-orange-500 bg-orange-500/5",
    badgeClass: "bg-orange-500/10 text-orange-500 border-orange-500/20",
  },
  medium: {
    label: "Medium",
    icon: AlertTriangle,
    rowClass: "border-l-4 border-amber-400 bg-amber-400/5",
    badgeClass: "bg-amber-400/10 text-amber-500 border-amber-400/20",
  },
  low: {
    label: "Low",
    icon: Info,
    rowClass: "border-l-4 border-emerald-500 bg-emerald-500/5",
    badgeClass: "bg-emerald-500/10 text-emerald-500 border-emerald-500/20",
  },
};

export function KpiAlertList({ data, isLoading, isError }: KpiAlertListProps) {
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <Card>
          <CardHeader><Skeleton className="h-5 w-32" /></CardHeader>
          <CardContent className="space-y-3">
            {[1, 2, 3, 4].map((i) => <Skeleton key={i} className="h-16 w-full" />)}
          </CardContent>
        </Card>
        <Card>
          <CardHeader><Skeleton className="h-5 w-40" /></CardHeader>
          <CardContent><Skeleton className="h-[260px] w-full" /></CardContent>
        </Card>
      </div>
    );
  }

  if (isError || !data) {
    return (
      <Card className="border-destructive/50">
        <CardContent className="flex h-40 items-center justify-center">
          <p className="text-sm text-muted-foreground">Failed to load alerts</p>
        </CardContent>
      </Card>
    );
  }

  const donutData = adaptSeverityDonut(data.summary.bySeverity);
  const { summary, alertList } = data;

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
      {/* Alert list */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle className="text-base">{alertList.title}</CardTitle>
          <Badge variant="secondary" className="text-xs">
            {summary.totalOpen} open
          </Badge>
        </CardHeader>
        <CardContent>
          <ScrollArea className="h-[320px] pr-2">
            <div className="space-y-2">
              {alertList.items.map((alert) => {
                const cfg = severityConfig[alert.severity];
                const Icon = cfg.icon;
                return (
                  <div key={alert.alertId} className={cn("rounded-lg p-3", cfg.rowClass)}>
                    <div className="flex items-start gap-2">
                      <Icon className="h-4 w-4 mt-0.5 shrink-0 opacity-70" />
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 flex-wrap">
                          <Badge variant="outline" className={cn("text-[10px] px-1.5 py-0", cfg.badgeClass)}>
                            {cfg.label}
                          </Badge>
                          <Badge variant="outline" className="text-[10px] px-1.5 py-0 text-muted-foreground">
                            {alert.alertType}
                          </Badge>
                        </div>
                        <p className="mt-1 text-sm">{alert.message}</p>
                        <p className="mt-0.5 text-[11px] text-muted-foreground">
                          Observed: {alert.observedValue} / Threshold: {alert.thresholdValue}
                        </p>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </ScrollArea>
        </CardContent>
      </Card>

      {/* Severity donut */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Alerts by Severity</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="relative">
            <ChartRenderer data={donutData} config={SEVERITY_DONUT_CONFIG} />
            {/* Center label */}
            <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
              <span className="text-3xl font-bold">{summary.totalOpen}</span>
              <span className="text-xs text-muted-foreground">open alerts</span>
            </div>
          </div>
          {/* Legend with counts */}
          <div className="mt-3 grid grid-cols-2 gap-2">
            {(["critical", "high", "medium", "low"] as AlertSeverity[]).map((sev) => {
              const cfg = severityConfig[sev];
              return (
                <div key={sev} className="flex items-center justify-between rounded-md px-3 py-1.5 text-xs bg-muted/40">
                  <span className="font-medium capitalize">{sev}</span>
                  <Badge variant="outline" className={cn("text-[10px]", cfg.badgeClass)}>
                    {data.summary.bySeverity[sev]}
                  </Badge>
                </div>
              );
            })}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
