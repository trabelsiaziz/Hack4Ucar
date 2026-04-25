"use client";

import { AlertCircle, AlertTriangle, CheckCircle, Info } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useWidgetData } from "@/hooks/useWidgetData";
import { cn } from "@/lib/utils";
import type { AlertItem, WidgetSchema } from "@/types";

interface AlertListWidgetProps {
  widget: WidgetSchema;
}

const alertIcons = {
  info: Info,
  warning: AlertTriangle,
  error: AlertCircle,
  success: CheckCircle,
};

const alertStyles = {
  info: "text-blue-500 bg-blue-500/10",
  warning: "text-amber-500 bg-amber-500/10",
  error: "text-red-500 bg-red-500/10",
  success: "text-emerald-500 bg-emerald-500/10",
};

export function AlertListWidget({ widget }: AlertListWidgetProps) {
  const { data, isLoading, isError } = useWidgetData<AlertItem[]>(widget.endpoint);
  const maxItems = (widget.props?.maxItems as number) || 5;

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <Skeleton className="h-5 w-32" />
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} className="h-16 w-full" />
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  if (isError || !data) {
    return (
      <Card className="border-destructive/50">
        <CardHeader>
          <CardTitle>{widget.title}</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">Failed to load alerts</p>
        </CardContent>
      </Card>
    );
  }

  const alerts = data.slice(0, maxItems);

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>{widget.title}</CardTitle>
        {data.filter((a) => !a.read).length > 0 && (
          <Badge variant="secondary">
            {data.filter((a) => !a.read).length} new
          </Badge>
        )}
      </CardHeader>
      <CardContent>
        {alerts.length === 0 ? (
          <p className="text-sm text-muted-foreground text-center py-8">
            No alerts at this time
          </p>
        ) : (
          <ScrollArea className="h-[300px]">
            <div className="space-y-3">
              {alerts.map((alert) => {
                const Icon = alertIcons[alert.type];
                return (
                  <div
                    key={alert.id}
                    className={cn(
                      "flex gap-3 rounded-lg p-3 transition-colors",
                      alert.read ? "bg-muted/50" : "bg-muted"
                    )}
                  >
                    <div
                      className={cn(
                        "flex h-8 w-8 shrink-0 items-center justify-center rounded-full",
                        alertStyles[alert.type]
                      )}
                    >
                      <Icon className="h-4 w-4" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2">
                        <p className="text-sm font-medium truncate">{alert.title}</p>
                        {!alert.read && (
                          <span className="h-2 w-2 rounded-full bg-primary shrink-0" />
                        )}
                      </div>
                      <p className="text-xs text-muted-foreground line-clamp-2 mt-0.5">
                        {alert.message}
                      </p>
                      <p className="text-xs text-muted-foreground/70 mt-1">
                        {new Date(alert.timestamp).toLocaleString()}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          </ScrollArea>
        )}
      </CardContent>
    </Card>
  );
}
