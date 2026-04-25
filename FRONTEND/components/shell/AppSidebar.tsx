"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ChevronRight } from "lucide-react";
import type { NavItem } from "@/types";
import { IconComponent } from "@/lib/icons";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from "@/components/ui/sidebar";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import type { UserContext } from "@/types";

interface AppSidebarProps {
  navigation: NavItem[];
  user: UserContext;
}

export function AppSidebar({ navigation, user }: AppSidebarProps) {
  const pathname = usePathname();

  const visibleNavigation = navigation
    .filter((item) => item.visible)
    .sort((a, b) => a.order - b.order);

  const isActive = (route: string) => pathname === route || pathname.startsWith(`${route}/`);

  const getInitials = (name: string) => {
    return name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <Sidebar collapsible="icon" className="border-r border-sidebar-border">
      <SidebarHeader className="border-b border-sidebar-border p-4">
        <Link href="/dashboard" className="flex items-center gap-3">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground font-bold text-sm">
            UC
          </div>
          <div className="flex flex-col group-data-[collapsible=icon]:hidden">
            <span className="text-sm font-semibold text-foreground">UCAR</span>
            <span className="text-xs text-muted-foreground">Management Platform</span>
          </div>
        </Link>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Navigation</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {visibleNavigation.map((item) => {
                const hasChildren = item.children.filter((c) => c.visible).length > 0;

                if (hasChildren) {
                  return (
                    <Collapsible
                      key={item.id}
                      asChild
                      defaultOpen={isActive(item.route)}
                      className="group/collapsible"
                    >
                      <SidebarMenuItem>
                        <CollapsibleTrigger asChild>
                          <SidebarMenuButton tooltip={item.label}>
                            <IconComponent name={item.icon} className="size-4" />
                            <span>{item.label}</span>
                            <ChevronRight className="ml-auto size-4 transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
                          </SidebarMenuButton>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                          <SidebarMenuSub>
                            {item.children
                              .filter((child) => child.visible)
                              .sort((a, b) => a.order - b.order)
                              .map((child) => (
                                <SidebarMenuSubItem key={child.id}>
                                  <SidebarMenuSubButton
                                    asChild
                                    isActive={isActive(child.route)}
                                  >
                                    <Link href={child.route}>
                                      <IconComponent name={child.icon} className="size-4" />
                                      <span>{child.label}</span>
                                    </Link>
                                  </SidebarMenuSubButton>
                                </SidebarMenuSubItem>
                              ))}
                          </SidebarMenuSub>
                        </CollapsibleContent>
                      </SidebarMenuItem>
                    </Collapsible>
                  );
                }

                return (
                  <SidebarMenuItem key={item.id}>
                    <SidebarMenuButton
                      asChild
                      isActive={isActive(item.route)}
                      tooltip={item.label}
                    >
                      <Link href={item.route}>
                        <IconComponent name={item.icon} className="size-4" />
                        <span>{item.label}</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter className="border-t border-sidebar-border p-2">
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton asChild tooltip={user.fullName}>
              <Link href="/profile" className="flex items-center gap-3">
                <Avatar className="h-7 w-7">
                  <AvatarImage src={user.avatarUrl} alt={user.fullName} />
                  <AvatarFallback className="text-xs bg-primary text-primary-foreground">
                    {getInitials(user.fullName)}
                  </AvatarFallback>
                </Avatar>
                <div className="flex flex-col group-data-[collapsible=icon]:hidden">
                  <span className="text-sm font-medium truncate">{user.fullName}</span>
                  <span className="text-xs text-muted-foreground truncate">
                    {user.roleCode.replace("_", " ")}
                  </span>
                </div>
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  );
}
