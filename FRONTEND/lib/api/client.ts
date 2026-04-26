// ─────────────────────────────────────────────────────────────────────────────
// Main API Client
// Calls http://localhost:8080 for all app data (users, nav, pages, widgets).
// Falls back to mock data when the backend is unreachable.
// ─────────────────────────────────────────────────────────────────────────────

import {
  mockUsers,
  currentUserKey,
  navigationByRole,
  pagesByRole,
  statCardData,
  chartData,
  tableData,
  alertData,
  recommendationData,
  profileData,
} from "@/mocks";
import type {
  UserContext,
  NavItem,
  PageSchema,
  StatCardData,
  TableData,
  AlertItem,
  RecommendationItem,
  ProfileSummaryData,
} from "@/types";

const BASE = "http://localhost:8080";

// ── helpers ───────────────────────────────────────────────────────────────────

async function get<T>(path: string, fallback: T): Promise<T> {
  try {
    const res = await fetch(`${BASE}${path}`, {
      headers: { Accept: "application/json" },
      signal: AbortSignal.timeout(5000),
    });
    if (!res.ok) {
      console.warn(`[api-client] ${path} → HTTP ${res.status}, using mock`);
      return fallback;
    }
    return (await res.json()) as T;
  } catch {
    console.warn(`[api-client] ${path} unreachable, using mock data`);
    return fallback;
  }
}

// ── Public API ────────────────────────────────────────────────────────────────

export const apiClient = {
  // Get current user context from /api/users/me
  async getCurrentUser(): Promise<UserContext> {
    return get<UserContext>("/api/users/me", mockUsers[currentUserKey]);
  },

  // Get navigation items for current user's role
  async getNavigation(roleCode: UserContext["roleCode"]): Promise<NavItem[]> {
    return get<NavItem[]>(
      `/api/navigation?role=${roleCode}`,
      navigationByRole[roleCode] || []
    );
  },

  // Get page schema from /api/pages/{pageId}?role={roleCode}
  async getPageSchema(
    roleCode: UserContext["roleCode"],
    pageId: string
  ): Promise<PageSchema | null> {
    const fallback = pagesByRole[roleCode]?.[pageId] || null;
    return get<PageSchema | null>(
      `/api/pages/${pageId}?role=${roleCode}`,
      fallback
    );
  },

  // Get widget data by endpoint (e.g. /api/widgets/gpa)
  async getWidgetData<T>(endpoint: string): Promise<T | null> {
    // Build mock fallback by checking all mock maps
    let fallback: T | null = null;
    if (endpoint in statCardData) fallback = statCardData[endpoint] as T;
    else if (endpoint in chartData) fallback = chartData[endpoint] as T;
    else if (endpoint in tableData) fallback = tableData[endpoint] as T;
    else if (endpoint in alertData) fallback = alertData[endpoint] as T;
    else if (endpoint in recommendationData) fallback = recommendationData[endpoint] as T;
    else if (endpoint in profileData) fallback = profileData[endpoint] as T;

    return get<T | null>(endpoint, fallback);
  },

  // ── Convenience typed methods ────────────────────────────────────────────────

  async getStatCardData(endpoint: string): Promise<StatCardData | null> {
    return this.getWidgetData<StatCardData>(endpoint);
  },

  async getChartData(endpoint: string): Promise<unknown[] | null> {
    return this.getWidgetData<unknown[]>(endpoint);
  },

  async getTableData(endpoint: string): Promise<TableData | null> {
    return this.getWidgetData<TableData>(endpoint);
  },

  async getAlertData(endpoint: string): Promise<AlertItem[] | null> {
    return this.getWidgetData<AlertItem[]>(endpoint);
  },

  async getRecommendationData(endpoint: string): Promise<RecommendationItem[] | null> {
    return this.getWidgetData<RecommendationItem[]>(endpoint);
  },

  async getProfileData(endpoint: string): Promise<ProfileSummaryData | null> {
    return this.getWidgetData<ProfileSummaryData>(endpoint);
  },
};
