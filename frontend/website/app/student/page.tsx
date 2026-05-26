import { DashboardPage } from "@/components/dashboard-page";

const summaryCards = [
  {
    title: "Today's lessons",
    value: "6",
    helper: "2 science, 1 language, 3 core lessons",
  },
  {
    title: "Pending homework",
    value: "2",
    helper: "One due today and one due tomorrow",
  },
  {
    title: "New grades",
    value: "1",
    helper: "Latest result posted in mathematics",
  },
  {
    title: "Attendance",
    value: "98%",
    helper: "One recorded absence this month",
  },
] as const;

const sections = [
  {
    title: "Today's schedule",
    items: [
      "08:30 — Mathematics · Room 201",
      "10:00 — Biology · Lab 3",
      "11:45 — History · Room 105",
    ],
    emptyMessage: "No lessons are scheduled for today.",
  },
  {
    title: "Assignments / homework",
    items: [
      "Submit biology worksheet before 16:00",
      "Read chapter 5 and prepare two discussion notes",
    ],
    emptyMessage: "No assignments are waiting right now.",
  },
  {
    title: "Grade highlights",
    items: ["Mathematics quiz — 92%", "Language presentation — teacher feedback ready"],
    emptyMessage: "No grades have been published yet.",
  },
  {
    title: "Announcements",
    items: [],
    emptyMessage: "No new announcements right now.",
  },
] as const;

const quickActions = ["View schedule", "Open homework", "Open gradebook", "Messages"] as const;

export default function StudentDashboardPage() {
  return (
    <DashboardPage
      contextLabel="School / class context"
      quickActions={[...quickActions]}
      role="student"
      sections={[...sections]}
      summaryCards={[...summaryCards]}
      title="Student dashboard"
    />
  );
}

