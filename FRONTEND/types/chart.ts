export type ChartType =
  | "bar"
  | "line"
  | "area"
  | "pie"
  | "donut"
  | "radar"
  | "stacked-bar"
  | "horizontal-bar";

export interface ChartSchema {
  type: ChartType;
  xKey?: string;
  yKeys?: string[];
  categoryKey?: string;
  dataKey?: string;
  colors?: string[];
  stacked?: boolean;
  showLegend?: boolean;
  showGrid?: boolean;
  height?: number;
}
