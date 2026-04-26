"use client";

import { Brain, Lightbulb, CheckCircle2, Clock, XCircle, ArrowRight } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { cn } from "@/lib/utils";
import type {
  InsightsDashboardDto,
  RecommendationPriority,
  RecommendationStatus,
  RecommendationCategory,
} from "@/types";

interface KpiInsightCardProps {
  data: InsightsDashboardDto | undefined;
  isLoading: boolean;
  isError?: boolean;
}

const priorityConfig: Record<RecommendationPriority, { label: string; cls: string }> = {
  urgent: { label: "Urgent", cls: "bg-red-600/10 text-red-600 border-red-600/20" },
  high:   { label: "High",   cls: "bg-orange-500/10 text-orange-500 border-orange-500/20" },
  medium: { label: "Medium", cls: "bg-amber-400/10 text-amber-500 border-amber-400/20" },
  low:    { label: "Low",    cls: "bg-sky-400/10 text-sky-500 border-sky-400/20" },
};

const categoryConfig: Record<RecommendationCategory, { cls: string }> = {
  ACADEMIC: { cls: "bg-violet-500/10 text-violet-500 border-violet-500/20" },
  FINANCE:  { cls: "bg-blue-500/10 text-blue-500 border-blue-500/20" },
  HR:       { cls: "bg-teal-500/10 text-teal-500 border-teal-500/20" },
};

const statusIcon: Record<RecommendationStatus, React.ElementType> = {
  proposed:  Clock,
  accepted:  CheckCircle2,
  rejected:  XCircle,
  completed: CheckCircle2,
};

export function KpiInsightCard({ data, isLoading, isError }: KpiInsightCardProps) {
  if (isLoading) {
    return (
      <div className="space-y-4">
        <Card>
          <CardHeader><Skeleton className="h-5 w-56" /></CardHeader>
          <CardContent className="space-y-3">
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-4/5" />
            <Skeleton className="h-3 w-32 mt-2" />
            <Skeleton className="h-2 w-full" />
          </CardContent>
        </Card>
        <Card>
          <CardHeader><Skeleton className="h-5 w-44" /></CardHeader>
          <CardContent className="space-y-3">
            {[1, 2, 3].map((i) => <Skeleton key={i} className="h-20 w-full" />)}
          </CardContent>
        </Card>
      </div>
    );
  }

  if (isError || !data) {
    return (
      <Card className="border-destructive/50">
        <CardContent className="flex h-40 items-center justify-center">
          <p className="text-sm text-muted-foreground">Failed to load insights</p>
        </CardContent>
      </Card>
    );
  }

  const { executiveSummary, topRecommendations } = data;
  const confidence = Math.round(executiveSummary.confidenceScore * 100);

  return (
    <div className="space-y-4">
      {/* Executive summary card */}
      <Card className="border-primary/20 bg-primary/[0.02]">
        <CardHeader className="flex flex-row items-center gap-2 pb-3">
          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary/10">
            <Brain className="h-4 w-4 text-primary" />
          </div>
          <div>
            <CardTitle className="text-base">{executiveSummary.title}</CardTitle>
            <p className="text-[11px] text-muted-foreground mt-0.5">
              AI-generated · Confidence {confidence}%
            </p>
          </div>
        </CardHeader>
        <CardContent className="space-y-3">
          <p className="text-sm leading-relaxed text-muted-foreground">
            {executiveSummary.summaryText}
          </p>
          <div className="space-y-1">
            <div className="flex justify-between text-xs">
              <span className="text-muted-foreground">Model confidence</span>
              <span className="font-medium">{confidence}%</span>
            </div>
            <Progress value={confidence} className="h-1.5" />
          </div>
        </CardContent>
      </Card>

      {/* Recommendations list */}
      <Card>
        <CardHeader className="flex flex-row items-center gap-2 pb-3">
          <Lightbulb className="h-5 w-5 text-amber-500" />
          <CardTitle className="text-base">{topRecommendations.title}</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {topRecommendations.items.slice(0, 5).map((rec) => {
            const StatusIcon = statusIcon[rec.status];
            const priCfg = priorityConfig[rec.priority];
            const catCfg = categoryConfig[rec.category];
            return (
              <div
                key={rec.recommendationId}
                className="rounded-lg border p-3 transition-colors hover:bg-muted/30"
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap mb-1.5">
                      <Badge variant="outline" className={cn("text-[10px] px-1.5 py-0", priCfg.cls)}>
                        {priCfg.label}
                      </Badge>
                      <Badge variant="outline" className={cn("text-[10px] px-1.5 py-0", catCfg.cls)}>
                        {rec.category}
                      </Badge>
                    </div>
                    <h4 className="text-sm font-medium">{rec.title}</h4>
                    <p className="mt-0.5 text-xs text-muted-foreground line-clamp-2">
                      {rec.description}
                    </p>
                  </div>
                  <div className="flex items-center gap-1 shrink-0">
                    <StatusIcon className="h-4 w-4 text-muted-foreground" />
                    <ArrowRight className="h-3.5 w-3.5 text-muted-foreground/50" />
                  </div>
                </div>
              </div>
            );
          })}
        </CardContent>
      </Card>
    </div>
  );
}
