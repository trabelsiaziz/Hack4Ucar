"use client";

import {
  LayoutDashboard,
  User,
  GraduationCap,
  FileText,
  Calendar,
  BookOpen,
  Library,
  Users,
  ClipboardCheck,
  BarChart3,
  Building2,
  UserCog,
  Briefcase,
  TrendingUp,
  UserPlus,
  Bell,
  Target,
  Settings,
  Sliders,
  Activity,
  Award,
  HeartPulse,
  AlertTriangle,
  AlertCircle,
  CheckCircle,
  Info,
  type LucideIcon,
} from "lucide-react";

const iconMap: Record<string, LucideIcon> = {
  LayoutDashboard,
  User,
  GraduationCap,
  FileText,
  Calendar,
  BookOpen,
  Library,
  Users,
  ClipboardCheck,
  BarChart3,
  Building2,
  UserCog,
  Briefcase,
  TrendingUp,
  UserPlus,
  Bell,
  Target,
  Settings,
  Sliders,
  Activity,
  Award,
  HeartPulse,
  AlertTriangle,
  AlertCircle,
  CheckCircle,
  Info,
};

export function getIcon(name: string): LucideIcon {
  return iconMap[name] || LayoutDashboard;
}

export function IconComponent({ name, className }: { name: string; className?: string }) {
  const Icon = getIcon(name);
  return <Icon className={className} />;
}
