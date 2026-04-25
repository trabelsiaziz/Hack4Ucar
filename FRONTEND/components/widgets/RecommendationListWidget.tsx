"use client";

import Link from "next/link";
import { Lightbulb, ChevronRight } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useWidgetData } from "@/hooks/useWidgetData";
import { cn } from "@/lib/utils";
import type { RecommendationItem, WidgetSchema } from "@/types";

interface RecommendationListWidgetProps {
  widget: WidgetSchema;
}

const priorityStyles = {
  high: "border-l-red-500 bg-red-500/5",
  medium: "border-l-amber-500 bg-amber-500/5",
  low: "border-l-blue-500 bg-blue-500/5",
};

const priorityBadge = {
  high: "bg-red-500/10 text-red-500 hover:bg-red-500/20",
  medium: "bg-amber-500/10 text-amber-500 hover:bg-amber-500/20",
  low: "bg-blue-500/10 text-blue-500 hover:bg-blue-500/20",
};

export function RecommendationListWidget({ widget }: RecommendationListWidgetProps) {
  const { data, isLoading, isError } = useWidgetData<RecommendationItem[]>(widget.endpoint);

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <Skeleton className="h-5 w-40" />
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} className="h-20 w-full" />
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
          <p className="text-sm text-muted-foreground">Failed to load recommendations</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center gap-2">
        <Lightbulb className="h-5 w-5 text-amber-500" />
        <CardTitle>{widget.title}</CardTitle>
      </CardHeader>
      <CardContent>
        {data.length === 0 ? (
          <p className="text-sm text-muted-foreground text-center py-8">
            No recommendations at this time
          </p>
        ) : (
          <div className="space-y-3">
            {data.map((rec) => (
              <div
                key={rec.id}
                className={cn(
                  "rounded-lg border-l-4 p-4",
                  priorityStyles[rec.priority]
                )}
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <h4 className="text-sm font-medium">{rec.title}</h4>
                      <Badge variant="outline" className={priorityBadge[rec.priority]}>
                        {rec.priority}
                      </Badge>
                    </div>
                    <p className="text-xs text-muted-foreground line-clamp-2">
                      {rec.description}
                    </p>
                  </div>
                  {rec.actionLabel && rec.actionRoute && (
                    <Button variant="ghost" size="sm" asChild className="shrink-0">
                      <Link href={rec.actionRoute}>
                        {rec.actionLabel}
                        <ChevronRight className="ml-1 h-4 w-4" />
                      </Link>
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
