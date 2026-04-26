"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { ChartRenderer } from "@/components/charts";
import { adaptChartDto } from "@/lib/api/dto-adapters";
import type { ChartDto } from "@/types";

interface KpiChartWidgetProps {
  dto: ChartDto | undefined;
  isLoading: boolean;
  isError?: boolean;
  height?: number;
}

export function KpiChartWidget({ dto, isLoading, isError, height }: KpiChartWidgetProps) {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <Skeleton className="h-5 w-48" />
        </CardHeader>
        <CardContent>
          <Skeleton className="h-[300px] w-full" />
        </CardContent>
      </Card>
    );
  }

  if (isError || !dto) {
    return (
      <Card className="border-destructive/50">
        <CardContent className="flex h-[300px] items-center justify-center">
          <p className="text-sm text-muted-foreground">Failed to load chart data</p>
        </CardContent>
      </Card>
    );
  }

  const { data, config } = adaptChartDto(dto);
  const finalConfig = height ? { ...config, height } : config;

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">{dto.title}</CardTitle>
      </CardHeader>
      <CardContent>
        <ChartRenderer data={data} config={finalConfig} />
      </CardContent>
    </Card>
  );
}
