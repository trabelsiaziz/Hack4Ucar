"use client";

import { ShieldX } from "lucide-react";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import type { UserContext } from "@/types";
import { canAccessRoute } from "@/lib/access/permissions";

interface AccessGuardProps {
  children: React.ReactNode;
  user: UserContext | undefined;
  route: string;
  isLoading?: boolean;
}

export function AccessGuard({ children, user, route, isLoading }: AccessGuardProps) {
  if (isLoading) {
    return null; // Parent should handle loading state
  }

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-200px)] text-center px-4">
        <ShieldX className="h-16 w-16 text-muted-foreground mb-4" />
        <h2 className="text-2xl font-bold mb-2">Authentication Required</h2>
        <p className="text-muted-foreground mb-6 max-w-md">
          You need to sign in to access this page.
        </p>
        <Button asChild>
          <Link href="/login">Sign In</Link>
        </Button>
      </div>
    );
  }

  if (!canAccessRoute(user, route)) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-200px)] text-center px-4">
        <ShieldX className="h-16 w-16 text-destructive mb-4" />
        <h2 className="text-2xl font-bold mb-2">Access Denied</h2>
        <p className="text-muted-foreground mb-6 max-w-md">
          You don&apos;t have permission to access this page. Please contact your
          administrator if you believe this is an error.
        </p>
        <Button asChild variant="outline">
          <Link href="/dashboard">Go to Dashboard</Link>
        </Button>
      </div>
    );
  }

  return <>{children}</>;
}
