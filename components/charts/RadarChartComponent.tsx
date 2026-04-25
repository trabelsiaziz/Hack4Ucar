"use client";

import {
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Radar,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import type { ChartSchema } from "@/types";

interface RadarChartComponentProps {
  data: unknown[];
  config: ChartSchema;
}

const COLORS = [
  "hsl(var(--chart-1))",
  "hsl(var(--chart-2))",
  "hsl(var(--chart-3))",
  "hsl(var(--chart-4))",
  "hsl(var(--chart-5))",
];

export function RadarChartComponent({ data, config }: RadarChartComponentProps) {
  const colors = config.colors || COLORS;
  const height = config.height || 300;

  return (
    <ResponsiveContainer width="100%" height={height}>
      <RadarChart data={data} cx="50%" cy="50%" outerRadius="80%">
        <PolarGrid className="stroke-muted" />
        <PolarAngleAxis
          dataKey={config.categoryKey || "name"}
          tick={{ fontSize: 12 }}
          className="fill-muted-foreground"
        />
        <PolarRadiusAxis tick={{ fontSize: 10 }} className="fill-muted-foreground" />
        <Tooltip
          contentStyle={{
            backgroundColor: "hsl(var(--background))",
            border: "1px solid hsl(var(--border))",
            borderRadius: "var(--radius)",
          }}
        />
        {config.showLegend && <Legend />}
        {config.yKeys?.map((key, index) => (
          <Radar
            key={key}
            name={key}
            dataKey={key}
            stroke={colors[index % colors.length]}
            fill={colors[index % colors.length]}
            fillOpacity={0.3}
          />
        ))}
      </RadarChart>
    </ResponsiveContainer>
  );
}
