"use client";

import useSWR from "swr";
import { apiClient } from "@/lib/api/client";

export function useWidgetData<T>(endpoint: string | null) {
  const { data, error, isLoading, mutate } = useSWR<T | null>(
    endpoint ? ["widget-data", endpoint] : null,
    () => apiClient.getWidgetData<T>(endpoint!),
    {
      revalidateOnFocus: false,
      dedupingInterval: 30000,
    }
  );

  return {
    data,
    isLoading,
    isError: !!error,
    error,
    mutate,
  };
}
