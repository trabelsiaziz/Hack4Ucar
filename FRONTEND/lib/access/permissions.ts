import type { UserContext } from "@/types";

export function hasPermission(user: UserContext | undefined, permission: string): boolean {
  if (!user) return false;
  return user.permissions.includes(permission);
}

export function hasAnyPermission(
  user: UserContext | undefined,
  permissions: string[]
): boolean {
  if (!user) return false;
  return permissions.some((p) => user.permissions.includes(p));
}

export function hasAllPermissions(
  user: UserContext | undefined,
  permissions: string[]
): boolean {
  if (!user) return false;
  return permissions.every((p) => user.permissions.includes(p));
}

export function hasFeatureFlag(user: UserContext | undefined, flag: string): boolean {
  if (!user) return false;
  return user.featureFlags.includes(flag);
}

export function canAccessRoute(user: UserContext | undefined, route: string): boolean {
  if (!user) return false;

  // Map routes to required permissions
  const routePermissions: Record<string, string[]> = {
    "/dashboard": ["view:dashboard"],
    "/profile": ["view:profile"],
    "/institutions": ["manage:institutions"],
    "/users": ["manage:users"],
    "/analytics": ["view:analytics"],
    "/alerts": ["view:alerts"],
    "/kpis": ["view:kpis"],
    "/system": ["manage:system"],
  };

  // Find matching route pattern
  for (const [pattern, permissions] of Object.entries(routePermissions)) {
    if (route === pattern || route.startsWith(`${pattern}/`)) {
      return hasAnyPermission(user, permissions);
    }
  }

  // Allow access by default for unmapped routes
  return true;
}
