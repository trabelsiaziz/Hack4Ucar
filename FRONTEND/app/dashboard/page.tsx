"use client";

import { AppShell } from "@/components/shell";
import { PageRenderer } from "@/components/dynamic";
import { AccessGuard } from "@/components/guards";
import { useUserContext } from "@/hooks/useUserContext";
import { useNavigation } from "@/hooks/useNavigation";
import { usePageSchema } from "@/hooks/usePageSchema";
import { Skeleton } from "@/components/ui/skeleton";

export default function DashboardPage() {
  const { user, isLoading: userLoading } = useUserContext();
  const { navigation, isLoading: navLoading } = useNavigation(user?.roleCode);
  const { pageSchema, isLoading: pageLoading } = usePageSchema(user?.roleCode, "dashboard");

  const isLoading = userLoading || navLoading;

  if (isLoading) {
    return (
      <div className="flex h-screen">
        <div className="w-64 border-r p-4 space-y-4">
          <Skeleton className="h-8 w-32" />
          <div className="space-y-2">
            {Array.from({ length: 6 }).map((_, i) => (
              <Skeleton key={i} className="h-10 w-full" />
            ))}
          </div>
        </div>
        <div className="flex-1 p-6 space-y-6">
          <Skeleton className="h-8 w-64" />
          <div className="grid gap-4 md:grid-cols-4">
            {Array.from({ length: 4 }).map((_, i) => (
              <Skeleton key={i} className="h-32" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <AccessGuard user={user} route="/dashboard" isLoading={false}>
        <div />
      </AccessGuard>
    );
  }

  return (
    <AppShell navigation={navigation} user={user} pageTitle="Dashboard">
      <AccessGuard user={user} route="/dashboard">
        <PageRenderer pageSchema={pageSchema} isLoading={pageLoading} />
      </AccessGuard>
    </AppShell>
  );
}
