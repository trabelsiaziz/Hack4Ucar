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

// Simulate API delay
const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export const apiClient = {
  // Get current user context
  async getCurrentUser(): Promise<UserContext> {
    await delay(100);
    return mockUsers[currentUserKey];
  },

  // Get navigation items for current user
  async getNavigation(roleCode: UserContext["roleCode"]): Promise<NavItem[]> {
    await delay(50);
    return navigationByRole[roleCode] || [];
  },

  // Get page schema
  async getPageSchema(
    roleCode: UserContext["roleCode"],
    pageId: string
  ): Promise<PageSchema | null> {
    await delay(100);
    const pages = pagesByRole[roleCode];
    return pages?.[pageId] || null;
  },

  // Get widget data by endpoint
  async getWidgetData<T>(endpoint: string): Promise<T | null> {
    await delay(150);

    // Check stat card data
    if (endpoint in statCardData) {
      return statCardData[endpoint] as T;
    }

    // Check chart data
    if (endpoint in chartData) {
      return chartData[endpoint] as T;
    }

    // Check table data
    if (endpoint in tableData) {
      return tableData[endpoint] as T;
    }

    // Check alert data
    if (endpoint in alertData) {
      return alertData[endpoint] as T;
    }

    // Check recommendation data
    if (endpoint in recommendationData) {
      return recommendationData[endpoint] as T;
    }

    // Check profile data
    if (endpoint in profileData) {
      return profileData[endpoint] as T;
    }

    return null;
  },

  // Convenience methods for specific widget types
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
