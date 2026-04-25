"use client";

import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";
import type { ChartSchema } from "@/types";

interface DonutChartComponentProps {
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

export function DonutChartComponent({ data, config }: DonutChartComponentProps) {
  const colors = config.colors || COLORS;
  const height = config.height || 300;

  return (
    <ResponsiveContainer width="100%" height={height}>
      <PieChart>
        <Pie
          data={data}
          dataKey={config.dataKey || "value"}
          nameKey={config.categoryKey || "name"}
          cx="50%"
          cy="50%"
          outerRadius={100}
          innerRadius={60}
          paddingAngle={2}
        >
          {data.map((_, index) => (
            <Cell key={`cell-${index}`} fill={colors[index % colors.length]} />
          ))}
        </Pie>
        <Tooltip
          contentStyle={{
            backgroundColor: "hsl(var(--background))",
            border: "1px solid hsl(var(--border))",
            borderRadius: "var(--radius)",
          }}
        />
        {config.showLegend && <Legend />}
      </PieChart>
    </ResponsiveContainer>
  );
}
