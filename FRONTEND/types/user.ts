export type RoleCode = "STUDENT" | "TEACHER" | "INSTITUTION_ADMIN" | "PLATFORM_ADMIN";

export interface UserContext {
  userId: string;
  fullName: string;
  email: string;
  avatarUrl?: string;
  roleCode: RoleCode;
  tenantId: string;
  institutionId: string;
  institutionName: string;
  orgUnitId?: string;
  permissions: string[];
  featureFlags: string[];
}
