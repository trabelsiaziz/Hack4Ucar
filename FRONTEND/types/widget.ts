import type { ChartSchema } from "./chart";

export type WidgetType =
  | "stat-card"
  | "table"
  | "chart"
  | "alert-list"
  | "recommendation-list"
  | "profile-summary";

export interface WidgetSchema {
  widgetId: string;
  widgetType: WidgetType;
  title: string;
  endpoint: string;
  visible: boolean;
  order: number;
  props: Record<string, unknown>;
}

export interface ChartWidgetProps {
  chart: ChartSchema;
}

export interface StatCardData {
  label: string;
  value: string | number;
  change?: number;
  changeLabel?: string;
  icon?: string;
}

export interface TableColumn {
  key: string;
  label: string;
  sortable?: boolean;
}

export interface TableData {
  columns: TableColumn[];
  rows: Record<string, unknown>[];
  pagination?: {
    page: number;
    pageSize: number;
    total: number;
  };
}

export interface AlertItem {
  id: string;
  type: "info" | "warning" | "error" | "success";
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
}

export interface RecommendationItem {
  id: string;
  title: string;
  description: string;
  priority: "high" | "medium" | "low";
  actionLabel?: string;
  actionRoute?: string;
}

export interface ProfileSummaryData {
  name: string;
  email: string;
  role: string;
  institution: string;
  avatarUrl?: string;
  stats: { label: string; value: string | number }[];
}
