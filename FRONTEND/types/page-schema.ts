import type { WidgetSchema } from "./widget";

export type LayoutType = "grid" | "stack";

export interface PageSchema {
  pageId: string;
  title: string;
  layout: LayoutType;
  widgets: WidgetSchema[];
}

export interface ActionSchema {
  actionId: string;
  label: string;
  actionType: string;
  endpoint: string;
  method: "GET" | "POST";
  visible: boolean;
}
