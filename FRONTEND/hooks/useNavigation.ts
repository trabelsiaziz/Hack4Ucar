"use client";

import useSWR from "swr";
import { apiClient } from "@/lib/api/client";
import type { NavItem, RoleCode } from "@/types";

export function useNavigation(roleCode: RoleCode | undefined) {
  const { data, error, isLoading } = useSWR<NavItem[]>(
    roleCode ? ["navigation", roleCode] : null,
    () => apiClient.getNavigation(roleCode!),
    {
      revalidateOnFocus: false,
      dedupingInterval: 60000,
    }
  );

  return {
    navigation: data || [],
    isLoading,
    isError: !!error,
    error,
  };
}
