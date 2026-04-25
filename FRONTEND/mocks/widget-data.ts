import type {
  StatCardData,
  TableData,
  AlertItem,
  RecommendationItem,
  ProfileSummaryData,
} from "@/types";

// Stat Card Data
export const statCardData: Record<string, StatCardData> = {
  "/api/widgets/gpa": {
    label: "Current GPA",
    value: "3.78",
    change: 0.12,
    changeLabel: "from last semester",
    icon: "TrendingUp",
  },
  "/api/widgets/credits": {
    label: "Credits Earned",
    value: 96,
    change: 18,
    changeLabel: "this semester",
    icon: "Award",
  },
  "/api/widgets/exams": {
    label: "Upcoming Exams",
    value: 3,
    icon: "Calendar",
  },
  "/api/widgets/total-students": {
    label: "Total Students",
    value: "12,458",
    change: 8.2,
    changeLabel: "from last year",
    icon: "Users",
  },
  "/api/widgets/total-teachers": {
    label: "Total Teachers",
    value: 847,
    change: 3.1,
    changeLabel: "from last year",
    icon: "UserCog",
  },
  "/api/widgets/graduation-rate": {
    label: "Graduation Rate",
    value: "87.3%",
    change: 2.4,
    changeLabel: "improvement",
    icon: "GraduationCap",
  },
  "/api/widgets/avg-gpa": {
    label: "Average GPA",
    value: "3.24",
    change: 0.08,
    changeLabel: "from last semester",
    icon: "TrendingUp",
  },
  "/api/widgets/total-institutions": {
    label: "Total Institutions",
    value: 14,
    icon: "Building2",
  },
  "/api/widgets/total-users": {
    label: "Total Users",
    value: "89,234",
    change: 12.5,
    changeLabel: "from last year",
    icon: "Users",
  },
  "/api/widgets/active-sessions": {
    label: "Active Sessions",
    value: "2,847",
    icon: "Activity",
  },
  "/api/widgets/system-health": {
    label: "System Health",
    value: "99.9%",
    icon: "HeartPulse",
  },
};

// Chart Data
export const chartData: Record<string, unknown[]> = {
  "/api/widgets/grade-trends": [
    { semester: "Fall 2023", gpa: 3.45 },
    { semester: "Spring 2024", gpa: 3.52 },
    { semester: "Fall 2024", gpa: 3.68 },
    { semester: "Spring 2025", gpa: 3.78 },
  ],
  "/api/widgets/enrollment-trends": [
    { year: "2020", students: 9800, graduates: 1850 },
    { year: "2021", students: 10200, graduates: 1920 },
    { year: "2022", students: 10850, graduates: 2010 },
    { year: "2023", students: 11500, graduates: 2150 },
    { year: "2024", students: 12100, graduates: 2280 },
    { year: "2025", students: 12458, graduates: 2350 },
  ],
  "/api/widgets/department-distribution": [
    { department: "Computer Science", students: 2450 },
    { department: "Engineering", students: 3200 },
    { department: "Business", students: 2100 },
    { department: "Medicine", students: 1800 },
    { department: "Arts", students: 1458 },
    { department: "Sciences", students: 1450 },
  ],
  "/api/widgets/performance": [
    { department: "CS", avgGpa: 3.42 },
    { department: "ENG", avgGpa: 3.28 },
    { department: "BUS", avgGpa: 3.35 },
    { department: "MED", avgGpa: 3.65 },
    { department: "ART", avgGpa: 3.18 },
    { department: "SCI", avgGpa: 3.52 },
  ],
  "/api/widgets/institution-users": [
    { institution: "Faculty of Sciences", users: 4500 },
    { institution: "Faculty of Engineering", users: 3800 },
    { institution: "Faculty of Medicine", users: 2900 },
    { institution: "Faculty of Business", users: 2400 },
    { institution: "Faculty of Arts", users: 1800 },
  ],
  "/api/widgets/platform-growth": [
    { month: "Jan", students: 850, teachers: 45, admins: 12 },
    { month: "Feb", students: 920, teachers: 52, admins: 8 },
    { month: "Mar", students: 780, teachers: 38, admins: 15 },
    { month: "Apr", students: 1100, teachers: 65, admins: 10 },
    { month: "May", students: 950, teachers: 48, admins: 7 },
    { month: "Jun", students: 680, teachers: 32, admins: 5 },
  ],
};

// Table Data
export const tableData: Record<string, TableData> = {
  "/api/widgets/recent-users": {
    columns: [
      { key: "name", label: "Name", sortable: true },
      { key: "email", label: "Email", sortable: true },
      { key: "role", label: "Role", sortable: true },
      { key: "date", label: "Registration Date", sortable: true },
    ],
    rows: [
      { name: "Yassine Mejri", email: "y.mejri@ucar.tn", role: "Student", date: "2025-04-24" },
      { name: "Amira Chaabane", email: "a.chaabane@ucar.tn", role: "Student", date: "2025-04-23" },
      { name: "Karim Boussaid", email: "k.boussaid@ucar.tn", role: "Teacher", date: "2025-04-22" },
      { name: "Nour Sellami", email: "n.sellami@ucar.tn", role: "Student", date: "2025-04-21" },
      { name: "Rim Hammami", email: "r.hammami@ucar.tn", role: "Student", date: "2025-04-20" },
    ],
    pagination: { page: 1, pageSize: 5, total: 125 },
  },
  "/api/widgets/institutions-table": {
    columns: [
      { key: "name", label: "Institution", sortable: true },
      { key: "students", label: "Students", sortable: true },
      { key: "teachers", label: "Teachers", sortable: true },
      { key: "avgGpa", label: "Avg GPA", sortable: true },
    ],
    rows: [
      { name: "Faculty of Sciences of Tunis", students: 4500, teachers: 285, avgGpa: 3.42 },
      { name: "National Engineering School", students: 3800, teachers: 220, avgGpa: 3.55 },
      { name: "Faculty of Medicine", students: 2900, teachers: 195, avgGpa: 3.68 },
      { name: "Higher Business School", students: 2400, teachers: 145, avgGpa: 3.32 },
      { name: "Faculty of Arts & Humanities", students: 1800, teachers: 120, avgGpa: 3.18 },
    ],
    pagination: { page: 1, pageSize: 5, total: 14 },
  },
};

// Alert Data
export const alertData: Record<string, AlertItem[]> = {
  "/api/widgets/alerts": [
    {
      id: "alert-1",
      type: "warning",
      title: "Low Attendance Detected",
      message: "Computer Science 301 has below 70% attendance this week.",
      timestamp: "2025-04-25T10:30:00Z",
      read: false,
    },
    {
      id: "alert-2",
      type: "info",
      title: "Registration Period Ending",
      message: "Course registration closes in 3 days.",
      timestamp: "2025-04-25T09:15:00Z",
      read: false,
    },
    {
      id: "alert-3",
      type: "error",
      title: "Grade Submission Overdue",
      message: "5 courses have overdue grade submissions.",
      timestamp: "2025-04-24T16:45:00Z",
      read: true,
    },
    {
      id: "alert-4",
      type: "success",
      title: "Budget Approved",
      message: "Annual department budget has been approved.",
      timestamp: "2025-04-24T14:20:00Z",
      read: true,
    },
  ],
  "/api/widgets/platform-alerts": [
    {
      id: "alert-p1",
      type: "info",
      title: "System Maintenance Scheduled",
      message: "Planned maintenance on April 28th, 2:00 AM - 4:00 AM.",
      timestamp: "2025-04-25T11:00:00Z",
      read: false,
    },
    {
      id: "alert-p2",
      type: "warning",
      title: "High Server Load",
      message: "Database server experiencing higher than normal load.",
      timestamp: "2025-04-25T10:45:00Z",
      read: false,
    },
    {
      id: "alert-p3",
      type: "success",
      title: "Backup Completed",
      message: "Weekly backup completed successfully.",
      timestamp: "2025-04-25T06:00:00Z",
      read: true,
    },
  ],
};

// Recommendation Data
export const recommendationData: Record<string, RecommendationItem[]> = {
  "/api/widgets/recommendations": [
    {
      id: "rec-1",
      title: "Improve Math Performance",
      description: "Based on your recent quiz scores, consider joining the tutoring program for Calculus II.",
      priority: "high",
      actionLabel: "Find Tutors",
      actionRoute: "/tutoring",
    },
    {
      id: "rec-2",
      title: "Course Recommendation",
      description: "Students with similar profiles often excel in Data Structures. Consider enrolling next semester.",
      priority: "medium",
      actionLabel: "View Course",
      actionRoute: "/courses/data-structures",
    },
    {
      id: "rec-3",
      title: "Study Group Available",
      description: "A study group for your Physics class is meeting this Thursday.",
      priority: "low",
      actionLabel: "Join Group",
      actionRoute: "/study-groups",
    },
  ],
  "/api/widgets/admin-recommendations": [
    {
      id: "rec-a1",
      title: "Staffing Alert",
      description: "Computer Science department may need 2 additional instructors next semester based on enrollment projections.",
      priority: "high",
      actionLabel: "View Analysis",
      actionRoute: "/analytics/staffing",
    },
    {
      id: "rec-a2",
      title: "Retention Risk",
      description: "15 students identified as at-risk for dropping out. Early intervention recommended.",
      priority: "high",
      actionLabel: "View Students",
      actionRoute: "/students/at-risk",
    },
    {
      id: "rec-a3",
      title: "Resource Optimization",
      description: "Library usage data suggests extended hours on weekends could benefit 800+ students.",
      priority: "medium",
      actionLabel: "View Report",
      actionRoute: "/reports/resources",
    },
  ],
};

// Profile Data
export const profileData: Record<string, ProfileSummaryData> = {
  "/api/widgets/profile-summary": {
    name: "Ahmed Ben Salem",
    email: "ahmed.bensalem@ucar.tn",
    role: "Student",
    institution: "Faculty of Sciences of Tunis",
    stats: [
      { label: "GPA", value: "3.78" },
      { label: "Credits", value: 96 },
      { label: "Semester", value: "6th" },
    ],
  },
  "/api/widgets/profile-detail": {
    name: "Ahmed Ben Salem",
    email: "ahmed.bensalem@ucar.tn",
    role: "Student",
    institution: "Faculty of Sciences of Tunis",
    stats: [
      { label: "GPA", value: "3.78" },
      { label: "Credits", value: 96 },
      { label: "Semester", value: "6th" },
      { label: "Major", value: "Computer Science" },
      { label: "Minor", value: "Mathematics" },
    ],
  },
};
