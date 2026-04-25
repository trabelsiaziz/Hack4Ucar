"use client";

import { Skeleton } from "@/components/ui/skeleton";
import { WidgetRenderer } from "./WidgetRenderer";
import { cn } from "@/lib/utils";
import type { PageSchema } from "@/types";

interface PageRendererProps {
  pageSchema: PageSchema | null;
  isLoading?: boolean;
}

export function PageRenderer({ pageSchema, isLoading }: PageRendererProps) {
  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-8 w-64" />
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-32 w-full" />
          ))}
        </div>
        <div className="grid gap-4 md:grid-cols-2">
          {Array.from({ length: 2 }).map((_, i) => (
            <Skeleton key={i} className="h-64 w-full" />
          ))}
        </div>
      </div>
    );
  }

  if (!pageSchema) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-center">
        <h2 className="text-xl font-semibold text-muted-foreground">Page Not Found</h2>
        <p className="text-sm text-muted-foreground mt-2">
          The requested page configuration could not be loaded.
        </p>
      </div>
    );
  }

  // Sort widgets by order
  const sortedWidgets = [...pageSchema.widgets]
    .filter((w) => w.visible)
    .sort((a, b) => a.order - b.order);

  // Group widgets by type for better layout
  const statWidgets = sortedWidgets.filter((w) => w.widgetType === "stat-card");
  const chartWidgets = sortedWidgets.filter((w) => w.widgetType === "chart");
  const otherWidgets = sortedWidgets.filter(
    (w) => !["stat-card", "chart"].includes(w.widgetType)
  );

  const isGridLayout = pageSchema.layout === "grid";

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold tracking-tight">{pageSchema.title}</h1>

      {isGridLayout ? (
        <>
          {/* Stat Cards Row */}
          {statWidgets.length > 0 && (
            <div
              className={cn(
                "grid gap-4",
                statWidgets.length === 1 && "grid-cols-1",
                statWidgets.length === 2 && "grid-cols-1 sm:grid-cols-2",
                statWidgets.length === 3 && "grid-cols-1 sm:grid-cols-2 lg:grid-cols-3",
                statWidgets.length >= 4 && "grid-cols-1 sm:grid-cols-2 lg:grid-cols-4"
              )}
            >
              {statWidgets.map((widget) => (
                <WidgetRenderer key={widget.widgetId} widget={widget} />
              ))}
            </div>
          )}

          {/* Charts Row */}
          {chartWidgets.length > 0 && (
            <div
              className={cn(
                "grid gap-4",
                chartWidgets.length === 1 && "grid-cols-1",
                chartWidgets.length >= 2 && "grid-cols-1 lg:grid-cols-2"
              )}
            >
              {chartWidgets.map((widget) => (
                <WidgetRenderer key={widget.widgetId} widget={widget} />
              ))}
            </div>
          )}

          {/* Other Widgets */}
          {otherWidgets.length > 0 && (
            <div
              className={cn(
                "grid gap-4",
                otherWidgets.length === 1 && "grid-cols-1",
                otherWidgets.length >= 2 && "grid-cols-1 lg:grid-cols-2"
              )}
            >
              {otherWidgets.map((widget) => (
                <WidgetRenderer key={widget.widgetId} widget={widget} />
              ))}
            </div>
          )}
        </>
      ) : (
        // Stack layout - all widgets in a single column
        <div className="space-y-4">
          {sortedWidgets.map((widget) => (
            <WidgetRenderer key={widget.widgetId} widget={widget} />
          ))}
        </div>
      )}
    </div>
  );
}
