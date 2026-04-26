"use client";

import { TrendingUp, TrendingDown, Minus } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import type { StatCardDto, TrendDirection } from "@/types";

interface KpiStatCardProps {
  card: StatCardDto;
}

const trendConfig: Record<
  TrendDirection,
  { icon: React.ElementType; color: string; bg: string }
> = {
  up: { icon: TrendingUp, color: "text-emerald-500", bg: "bg-emerald-500/10" },
  down: { icon: TrendingDown, color: "text-red-500", bg: "bg-red-500/10" },
  stable: { icon: Minus, color: "text-sky-400", bg: "bg-sky-400/10" },
};

export function KpiStatCard({ card }: KpiStatCardProps) {
  const trend = card.trend ?? "stable";
  const cfg = trendConfig[trend] ?? trendConfig.stable;
  const Icon = cfg.icon;
  const value = card.value ?? null;
  const delta = card.delta ?? null;
  const showPlus = trend === "up" && (delta ?? 0) > 0;

  return (
    <Card className="relative overflow-hidden transition-shadow hover:shadow-md">
      {/* accent stripe */}
      <div
        className={cn(
          "absolute inset-y-0 left-0 w-1 rounded-l-lg",
          card.trend === "up" && "bg-emerald-500",
          card.trend === "down" && "bg-red-500",
          card.trend === "stable" && "bg-sky-400"
        )}
      />
      <CardContent className="pl-5 pt-5 pb-5 pr-4">
        <p className="text-xs font-medium text-muted-foreground uppercase tracking-wide truncate">
          {card.title}
        </p>
        <div className="mt-2 flex items-end gap-2">
          <span className="text-3xl font-bold tabular-nums">
            {value !== null ? value.toFixed(1) : "—"}
            <span className="text-lg font-normal text-muted-foreground ml-0.5">
              {card.unit}
            </span>
          </span>
        </div>
        <div className="mt-3 flex items-center gap-1.5">
          <span className={cn("flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-semibold", cfg.bg, cfg.color)}>
            <Icon className="h-3 w-3" />
            {showPlus && "+"}
            {delta !== null ? delta.toFixed(1) : "—"}{card.unit}
          </span>
          <span className="text-xs text-muted-foreground">vs prev. period</span>
        </div>
        <p className="mt-2 text-[11px] text-muted-foreground/60">
          {card.scope.scopeType === "institution"
            ? card.scope.scopeId
            : "All Institutions"}
        </p>
      </CardContent>
    </Card>
  );
}
