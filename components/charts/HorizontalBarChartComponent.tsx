"use client";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import type { ChartSchema } from "@/types";

interface HorizontalBarChartComponentProps {
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

export function HorizontalBarChartComponent({ data, config }: HorizontalBarChartComponentProps) {
  const colors = config.colors || COLORS;
  const height = config.height || 300;

  // For horizontal bar, we use the category key for Y axis
  const yAxisKey = config.yKeys?.[0] || config.categoryKey || "name";

  return (
    <ResponsiveContainer width="100%" height={height}>
      <BarChart
        data={data}
        layout="vertical"
        margin={{ top: 10, right: 30, left: 60, bottom: 0 }}
      >
        {config.showGrid && <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />}
        <XAxis
          type="number"
          tick={{ fontSize: 12 }}
          tickLine={false}
          axisLine={false}
          className="fill-muted-foreground"
        />
        <YAxis
          type="category"
          dataKey={yAxisKey}
          tick={{ fontSize: 12 }}
          tickLine={false}
          axisLine={false}
          className="fill-muted-foreground"
          width={80}
        />
        <Tooltip
          contentStyle={{
            backgroundColor: "hsl(var(--background))",
            border: "1px solid hsl(var(--border))",
            borderRadius: "var(--radius)",
          }}
        />
        {config.showLegend && <Legend />}
        <Bar
          dataKey={config.xKey || "value"}
          fill={colors[0]}
          radius={[0, 4, 4, 0]}
        />
      </BarChart>
    </ResponsiveContainer>
  );
}
