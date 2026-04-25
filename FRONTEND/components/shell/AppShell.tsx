"use client";

import { SidebarProvider, SidebarInset } from "@/components/ui/sidebar";
import { AppSidebar } from "./AppSidebar";
import { Topbar } from "./Topbar";
import type { NavItem, UserContext } from "@/types";

interface AppShellProps {
  children: React.ReactNode;
  navigation: NavItem[];
  user: UserContext;
  pageTitle?: string;
}

export function AppShell({ children, navigation, user, pageTitle }: AppShellProps) {
  return (
    <SidebarProvider>
      <AppSidebar navigation={navigation} user={user} />
      <SidebarInset>
        <Topbar user={user} pageTitle={pageTitle} />
        <main className="flex-1 overflow-auto p-4 md:p-6">{children}</main>
      </SidebarInset>
    </SidebarProvider>
  );
}
