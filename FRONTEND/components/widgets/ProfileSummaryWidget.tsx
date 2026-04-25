"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { useWidgetData } from "@/hooks/useWidgetData";
import type { ProfileSummaryData, WidgetSchema } from "@/types";

interface ProfileSummaryWidgetProps {
  widget: WidgetSchema;
}

export function ProfileSummaryWidget({ widget }: ProfileSummaryWidgetProps) {
  const { data, isLoading, isError } = useWidgetData<ProfileSummaryData>(widget.endpoint);

  const getInitials = (name: string) => {
    return name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase()
      .slice(0, 2);
  };

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <Skeleton className="h-5 w-32" />
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-4">
            <Skeleton className="h-16 w-16 rounded-full" />
            <div className="space-y-2">
              <Skeleton className="h-5 w-40" />
              <Skeleton className="h-4 w-32" />
            </div>
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
          <p className="text-sm text-muted-foreground">Failed to load profile</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{widget.title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4">
          <Avatar className="h-16 w-16">
            <AvatarImage src={data.avatarUrl} alt={data.name} />
            <AvatarFallback className="text-lg bg-primary text-primary-foreground">
              {getInitials(data.name)}
            </AvatarFallback>
          </Avatar>
          <div className="space-y-1">
            <h3 className="text-lg font-semibold">{data.name}</h3>
            <p className="text-sm text-muted-foreground">{data.email}</p>
            <div className="flex items-center gap-2">
              <Badge variant="secondary">{data.role}</Badge>
              <span className="text-sm text-muted-foreground">{data.institution}</span>
            </div>
          </div>
        </div>

        {data.stats && data.stats.length > 0 && (
          <>
            <Separator className="my-4" />
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
              {data.stats.map((stat, index) => (
                <div key={index} className="text-center">
                  <p className="text-2xl font-bold">{stat.value}</p>
                  <p className="text-xs text-muted-foreground">{stat.label}</p>
                </div>
              ))}
            </div>
          </>
        )}
      </CardContent>
    </Card>
  );
}
