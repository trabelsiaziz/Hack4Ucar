"use client";

import { TrendingUp, TrendingDown } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useWidgetData } from "@/hooks/useWidgetData";
import { IconComponent } from "@/lib/icons";
import type { StatCardData, WidgetSchema } from "@/types";

interface StatCardWidgetProps {
  widget: WidgetSchema;
}

export function StatCardWidget({ widget }: StatCardWidgetProps) {
  const { data, isLoading, isError } = useWidgetData<StatCardData>(widget.endpoint);

  if (isLoading) {
    return (
      <Card>
        <CardHeader className="flex flex-row items-center justify-between pb-2">
          <Skeleton className="h-4 w-24" />
          <Skeleton className="h-4 w-4" />
        </CardHeader>
        <CardContent>
          <Skeleton className="h-8 w-20 mb-2" />
          <Skeleton className="h-3 w-32" />
        </CardContent>
      </Card>
    );
  }

  if (isError || !data) {
    return (
      <Card className="border-destructive/50">
        <CardHeader className="flex flex-row items-center justify-between pb-2">
          <CardTitle className="text-sm font-medium">{widget.title}</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">Failed to load data</p>
        </CardContent>
      </Card>
    );
  }

  const iconName = (widget.props?.icon as string) || data.icon;
  const isPositive = data.change && data.change > 0;

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">
          {widget.title}
        </CardTitle>
        {iconName && (
          <IconComponent name={iconName} className="h-4 w-4 text-muted-foreground" />
        )}
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{data.value}</div>
        {data.change !== undefined && (
          <div className="flex items-center gap-1 text-xs mt-1">
            {isPositive ? (
              <TrendingUp className="h-3 w-3 text-emerald-500" />
            ) : (
              <TrendingDown className="h-3 w-3 text-red-500" />
            )}
            <span className={isPositive ? "text-emerald-500" : "text-red-500"}>
              {isPositive ? "+" : ""}
              {data.change}%
            </span>
            {data.changeLabel && (
              <span className="text-muted-foreground">{data.changeLabel}</span>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
