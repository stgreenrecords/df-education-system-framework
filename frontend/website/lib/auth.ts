export const AUTH_TOKEN_COOKIE = "df_access_token";

export type SupportedDashboardRoute = "/student" | "/teacher";
export type SupportedRole = "student" | "teacher";

export type CurrentUser = {
  userId: string;
  tenantId: string;
  username: string;
  displayName: string;
  authority: string;
  roles: string[];
};

function normalizeRoles(roles: string[]): string[] {
  return roles.map((role) => role.trim().toLowerCase()).filter(Boolean);
}

export function resolveSupportedDashboardRoute(user: Pick<CurrentUser, "roles">): SupportedDashboardRoute | null {
  const roles = normalizeRoles(user.roles);

  if (roles.includes("student")) {
    return "/student";
  }

  if (roles.includes("teacher")) {
    return "/teacher";
  }

  return null;
}

export function userHasRole(user: Pick<CurrentUser, "roles">, role: SupportedRole): boolean {
  return normalizeRoles(user.roles).includes(role);
}

export function formatDisplayName(user: Pick<CurrentUser, "displayName" | "username">): string {
  return user.displayName.trim() || user.username;
}

