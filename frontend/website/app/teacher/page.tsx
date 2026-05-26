import { DashboardPage } from "@/components/dashboard-page";

const summaryCards = [
  {
    title: "Classes today",
    value: "5",
    helper: "Three homeroom sessions and two labs",
  },
  {
    title: "Attendance pending",
    value: "1",
    helper: "Grade 8-B attendance still needs confirmation",
  },
  {
    title: "Homework to review",
    value: "7",
    helper: "Three urgent submissions due today",
  },
  {
    title: "Alerts",
    value: "2",
    helper: "One schedule change and one parent message",
  },
] as const;

const sections = [
  {
    title: "Today's teaching agenda",
    items: [
      "08:15 — Grade 7 Mathematics",
      "10:10 — Grade 8 Laboratory supervision",
      "13:30 — Staff preparation block",
    ],
    emptyMessage: "No lessons are scheduled for today.",
  },
  {
    title: "Class / group list",
    items: ["Grade 7-A · 26 students", "Grade 8-B · 24 students", "Science Club · 12 students"],
    emptyMessage: "No class groups are assigned right now.",
  },
  {
    title: "Attendance queue",
    items: ["Grade 8-B morning register not submitted"],
    emptyMessage: "All attendance registers are complete.",
  },
  {
    title: "Homework / assessment queue",
    items: [],
    emptyMessage: "No homework or assessments are waiting for review.",
  },
  {
    title: "Announcements / notices",
    items: ["Lab timetable updated for Thursday", "Fire drill reminder at 14:00"],
    emptyMessage: "No notices are active right now.",
  },
  {
    title: "Performance / status snapshot",
    items: ["82% homework completion this week", "3 students flagged for follow-up"],
    emptyMessage: "Performance snapshots are not available yet.",
  },
] as const;

const quickActions = ["Start attendance", "Create homework", "Open classes", "View notices"] as const;

export default function TeacherDashboardPage() {
  return (
    <DashboardPage
      contextLabel="Institution context"
      quickActions={[...quickActions]}
      role="teacher"
      sections={[...sections]}
      summaryCards={[...summaryCards]}
      title="Teacher dashboard"
    />
  );
}

