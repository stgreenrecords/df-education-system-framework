"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
import {
  CurrentUser,
  formatDisplayName,
  resolveSupportedDashboardRoute,
  SupportedRole,
  userHasRole,
} from "@/lib/auth";

type SummaryCard = {
  title: string;
  value: string;
  helper: string;
};

type SectionCard = {
  title: string;
  items: string[];
  emptyMessage: string;
};

type DashboardPageProps = {
  role: SupportedRole;
  title: string;
  contextLabel: string;
  summaryCards: SummaryCard[];
  sections: SectionCard[];
  quickActions: string[];
};

type DashboardStatus =
  | "loading"
  | "ready"
  | "error"
  | "unauthorized"
  | "unsupported"
  | "role-mismatch";

type ApiError = {
  message?: string;
};

async function readApiError(response: Response): Promise<string | null> {
  try {
    const payload = (await response.json()) as ApiError;
    return payload.message ?? null;
  } catch {
    return null;
  }
}

export function DashboardPage({
  role,
  title,
  contextLabel,
  summaryCards,
  sections,
  quickActions,
}: DashboardPageProps) {
  const router = useRouter();
  const [status, setStatus] = useState<DashboardStatus>("loading");
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [supportedRoute, setSupportedRoute] = useState<string | null>(null);

  const loadCurrentUser = useCallback(async () => {
    setStatus("loading");
    setErrorMessage("");

    try {
      const response = await fetch("/api/auth/me", {
        cache: "no-store",
      });

      if (response.status === 401) {
        setCurrentUser(null);
        setSupportedRoute(null);
        setStatus("unauthorized");
        return;
      }

      if (!response.ok) {
        setErrorMessage((await readApiError(response)) ?? "Unable to load the dashboard right now.");
        setCurrentUser(null);
        setSupportedRoute(null);
        setStatus("error");
        return;
      }

      const user = (await response.json()) as CurrentUser;
      const route = resolveSupportedDashboardRoute(user);

      setCurrentUser(user);
      setSupportedRoute(route);

      if (!userHasRole(user, role)) {
        setStatus(route ? "role-mismatch" : "unsupported");
        return;
      }

      setStatus("ready");
    } catch {
      setErrorMessage("Unable to reach the website session service right now.");
      setCurrentUser(null);
      setSupportedRoute(null);
      setStatus("error");
    }
  }, [role]);

  useEffect(() => {
    void loadCurrentUser();
  }, [loadCurrentUser]);

  async function handleLogout() {
    try {
      await fetch("/api/auth/logout", {
        method: "POST",
      });
    } finally {
      router.push("/login?loggedOut=1");
      router.refresh();
    }
  }

  return (
    <main className="page-shell">
      <section className="surface-card page-panel" aria-labelledby="dashboard-title">
        <header className="top-nav">
          <div>
            <p className="eyebrow">Dashboard header</p>
            <h1 id="dashboard-title">{title}</h1>
            <p className="muted-copy">
              {currentUser ? `Good day, ${formatDisplayName(currentUser)}` : "Good morning"} · {contextLabel}
            </p>
          </div>
          <div className="nav-actions">
            <button className="button-link" onClick={handleLogout} type="button">
              Sign out
            </button>
          </div>
        </header>

        {status === "loading" ? (
          <div className="content-stack" aria-live="polite">
            <div className="metric-grid">
              {summaryCards.map((card) => (
                <article className="metric-card metric-card--loading" key={card.title}>
                  <h2>{card.title}</h2>
                  <p>Loading...</p>
                </article>
              ))}
            </div>
            <div className="content-grid">
              {sections.map((section) => (
                <section className="content-card" key={section.title}>
                  <h2>{section.title}</h2>
                  <p className="muted-copy">Loading section data...</p>
                </section>
              ))}
            </div>
          </div>
        ) : null}

        {status === "unauthorized" ? (
          <section className="inline-message inline-message--neutral" aria-live="polite">
            <h2>Sign in required</h2>
            <p>You need to sign in before this dashboard can be shown.</p>
            <Link className="button-link button-link--primary" href="/login">
              Go to login
            </Link>
          </section>
        ) : null}

        {status === "role-mismatch" && currentUser ? (
          <section className="inline-message inline-message--neutral" aria-live="polite">
            <h2>Different dashboard available</h2>
            <p>
              Your account is signed in as {formatDisplayName(currentUser)}, but the current route does not
              match your supported preview dashboard.
            </p>
            {supportedRoute ? (
              <Link className="button-link button-link--primary" href={supportedRoute}>
                Open your supported dashboard
              </Link>
            ) : null}
          </section>
        ) : null}

        {status === "unsupported" ? (
          <section className="inline-message inline-message--neutral" aria-live="polite">
            <h2>Role not yet supported</h2>
            <p>
              Your account is authenticated, but this preview currently supports only student and teacher
              dashboards.
            </p>
            <Link className="button-link button-link--primary" href="/login">
              Return to login
            </Link>
          </section>
        ) : null}

        {status === "error" ? (
          <section className="inline-message inline-message--error" aria-live="assertive">
            <h2>Dashboard unavailable</h2>
            <p>{errorMessage}</p>
            <button className="button-link button-link--primary" onClick={() => void loadCurrentUser()} type="button">
              Retry
            </button>
          </section>
        ) : null}

        {status === "ready" && currentUser ? (
          <div className="content-stack">
            <section className="metric-grid" aria-label="Quick summary cards">
              {summaryCards.map((card) => (
                <article className="metric-card" key={card.title}>
                  <h2>{card.title}</h2>
                  <p className="metric-value">{card.value}</p>
                  <p className="muted-copy">{card.helper}</p>
                </article>
              ))}
            </section>

            <section className="content-grid" aria-label="Dashboard content blocks">
              {sections.map((section) => (
                <article className="content-card" key={section.title}>
                  <h2>{section.title}</h2>
                  {section.items.length > 0 ? (
                    <ul className="detail-list">
                      {section.items.map((item) => (
                        <li key={item}>{item}</li>
                      ))}
                    </ul>
                  ) : (
                    <p className="empty-state">{section.emptyMessage}</p>
                  )}
                </article>
              ))}
            </section>

            <section aria-label="Quick actions" className="actions-row">
              {quickActions.map((action) => (
                <button className="button-link" key={action} type="button">
                  {action}
                </button>
              ))}
            </section>
          </div>
        ) : null}
      </section>
    </main>
  );
}

