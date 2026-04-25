"use client";

import type { WidgetSchema, WidgetType } from "@/types";
import {
  StatCardWidget,
  ChartWidget,
  TableWidget,
  AlertListWidget,
  RecommendationListWidget,
  ProfileSummaryWidget,
} from "@/components/widgets";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

interface WidgetRendererProps {
  widget: WidgetSchema;
}

// Widget registry - maps widget types to their components
const widgetRegistry: Record<WidgetType, React.ComponentType<{ widget: WidgetSchema }>> = {
  "stat-card": StatCardWidget,
  chart: ChartWidget,
  table: TableWidget,
  "alert-list": AlertListWidget,
  "recommendation-list": RecommendationListWidget,
  "profile-summary": ProfileSummaryWidget,
};

export function WidgetRenderer({ widget }: WidgetRendererProps) {
  // Skip invisible widgets
  if (!widget.visible) {
    return null;
  }

  const WidgetComponent = widgetRegistry[widget.widgetType];

  if (!WidgetComponent) {
    return (
      <Card className="border-amber-500/50">
        <CardHeader>
          <CardTitle className="text-amber-500">Unknown Widget</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">
            Widget type &quot;{widget.widgetType}&quot; is not supported.
          </p>
        </CardContent>
      </Card>
    );
  }

  return <WidgetComponent widget={widget} />;
}
