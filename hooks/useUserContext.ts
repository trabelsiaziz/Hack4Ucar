"use client";

import useSWR from "swr";
import { apiClient } from "@/lib/api/client";
import type { UserContext } from "@/types";

export function useUserContext() {
  const { data, error, isLoading, mutate } = useSWR<UserContext>(
    "user-context",
    () => apiClient.getCurrentUser(),
    {
      revalidateOnFocus: false,
      dedupingInterval: 60000,
    }
  );

  return {
    user: data,
    isLoading,
    isError: !!error,
    error,
    mutate,
  };
}
