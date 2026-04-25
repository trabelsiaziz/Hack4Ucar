"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useWidgetData } from "@/hooks/useWidgetData";
import { ChartRenderer } from "@/components/charts";
import type { ChartSchema, WidgetSchema } from "@/types";

interface ChartWidgetProps {
  widget: WidgetSchema;
}

export function ChartWidget({ widget }: ChartWidgetProps) {
  const { data, isLoading, isError } = useWidgetData<unknown[]>(widget.endpoint);
  const chartConfig = widget.props?.chart as ChartSchema | undefined;

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <Skeleton className="h-5 w-40" />
        </CardHeader>
        <CardContent>
          <Skeleton className="h-[250px] w-full" />
        </CardContent>
      </Card>
    );
  }

  if (isError || !data || !chartConfig) {
    return (
      <Card className="border-destructive/50">
        <CardHeader>
          <CardTitle>{widget.title}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex h-[200px] items-center justify-center">
            <p className="text-sm text-muted-foreground">Failed to load chart data</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{widget.title}</CardTitle>
      </CardHeader>
      <CardContent>
        <ChartRenderer data={data} config={chartConfig} />
      </CardContent>
    </Card>
  );
}
