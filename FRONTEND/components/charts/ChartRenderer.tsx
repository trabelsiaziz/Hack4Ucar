"use client";

import type { ChartSchema } from "@/types";
import { BarChartComponent } from "./BarChartComponent";
import { LineChartComponent } from "./LineChartComponent";
import { AreaChartComponent } from "./AreaChartComponent";
import { PieChartComponent } from "./PieChartComponent";
import { DonutChartComponent } from "./DonutChartComponent";
import { RadarChartComponent } from "./RadarChartComponent";
import { HorizontalBarChartComponent } from "./HorizontalBarChartComponent";

interface ChartRendererProps {
  data: unknown[];
  config: ChartSchema;
}

export function ChartRenderer({ data, config }: ChartRendererProps) {
  switch (config.type) {
    case "bar":
      return <BarChartComponent data={data} config={config} />;
    case "stacked-bar":
      return <BarChartComponent data={data} config={{ ...config, stacked: true }} />;
    case "horizontal-bar":
      return <HorizontalBarChartComponent data={data} config={config} />;
    case "line":
      return <LineChartComponent data={data} config={config} />;
    case "area":
      return <AreaChartComponent data={data} config={config} />;
    case "pie":
      return <PieChartComponent data={data} config={config} />;
    case "donut":
      return <DonutChartComponent data={data} config={config} />;
    case "radar":
      return <RadarChartComponent data={data} config={config} />;
    default:
      return (
        <div className="flex h-[200px] items-center justify-center text-muted-foreground">
          Unsupported chart type: {config.type}
        </div>
      );
  }
}
