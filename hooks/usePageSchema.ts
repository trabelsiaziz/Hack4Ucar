"use client";

import useSWR from "swr";
import { apiClient } from "@/lib/api/client";
import type { PageSchema, RoleCode } from "@/types";

export function usePageSchema(roleCode: RoleCode | undefined, pageId: string) {
  const { data, error, isLoading } = useSWR<PageSchema | null>(
    roleCode ? ["page-schema", roleCode, pageId] : null,
    () => apiClient.getPageSchema(roleCode!, pageId),
    {
      revalidateOnFocus: false,
      dedupingInterval: 30000,
    }
  );

  return {
    pageSchema: data,
    isLoading,
    isError: !!error,
    error,
  };
}
