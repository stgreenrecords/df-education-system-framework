"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { FormEvent, useMemo, useState } from "react";
import { CurrentUser, resolveSupportedDashboardRoute } from "@/lib/auth";

type FormFeedback = {
  tone: "error" | "neutral";
  message: string;
};

type ApiError = {
  message?: string;
};

const INVALID_CREDENTIALS_MESSAGE = "The username or password is incorrect.";
const UNSUPPORTED_ROLE_MESSAGE =
  "Your account is authenticated, but this preview currently supports only student and teacher dashboards.";
const NETWORK_ERROR_MESSAGE = "Unable to reach the identity service right now. Please try again.";

async function readApiError(response: Response): Promise<string | null> {
  try {
    const payload = (await response.json()) as ApiError;
    return payload.message ?? null;
  } catch {
    return null;
  }
}

export function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [feedback, setFeedback] = useState<FormFeedback | null>(null);

  const loggedOutMessage = useMemo(() => {
    return searchParams.get("loggedOut") === "1"
      ? "You have been signed out. Sign in again to continue."
      : null;
  }, [searchParams]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsSubmitting(true);
    setFeedback(null);

    try {
      const loginResponse = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (!loginResponse.ok) {
        const apiMessage = await readApiError(loginResponse);
        setFeedback({
          tone: "error",
          message:
            apiMessage ??
            (loginResponse.status === 401
              ? INVALID_CREDENTIALS_MESSAGE
              : "Sign-in could not be completed. Please review your details and try again."),
        });
        return;
      }

      const currentUserResponse = await fetch("/api/auth/me", {
        cache: "no-store",
      });

      if (!currentUserResponse.ok) {
        const apiMessage = await readApiError(currentUserResponse);
        setFeedback({
          tone: "error",
          message: apiMessage ?? NETWORK_ERROR_MESSAGE,
        });
        return;
      }

      const currentUser = (await currentUserResponse.json()) as CurrentUser;
      const supportedRoute = resolveSupportedDashboardRoute(currentUser);

      if (!supportedRoute) {
        setFeedback({
          tone: "neutral",
          message: UNSUPPORTED_ROLE_MESSAGE,
        });
        return;
      }

      router.push(supportedRoute);
      router.refresh();
    } catch {
      setFeedback({
        tone: "error",
        message: NETWORK_ERROR_MESSAGE,
      });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="page-shell page-shell--centered">
      <section className="surface-card page-panel" aria-labelledby="login-page-title">
        <header className="top-nav">
          <div>
            <p className="eyebrow">Education System Framework</p>
            <h1 id="login-page-title">Account login</h1>
          </div>
          <nav aria-label="Login page navigation" className="nav-actions">
            <Link className="text-link" href="/">
              Back to home
            </Link>
            <Link className="text-link" href="/login#login-help">
              Help
            </Link>
          </nav>
        </header>

        <div className="split-layout">
          <section className="panel-block" aria-labelledby="login-intro-title">
            <p className="eyebrow">Intro / trust panel</p>
            <h2 id="login-intro-title">Sign in with your account credentials</h2>
            <p>
              Sign in with your account credentials to continue to your learning workspace.
            </p>
            <div className="info-stack">
              <div className="info-card">
                <h3>Security / access note</h3>
                <p>
                  This preview keeps the login flow aligned with the accepted backend identity endpoints
                  and routes only to the student and teacher dashboards currently in scope.
                </p>
              </div>
              <div className="info-card" id="login-help">
                <h3>Support / help text</h3>
                <p>
                  Use your issued account username and password. If you cannot sign in, return home,
                  verify your institution, or contact support.
                </p>
              </div>
            </div>
          </section>

          <section className="panel-block" aria-labelledby="login-form-title">
            <p className="eyebrow">Sign-in form</p>
            <h2 id="login-form-title">Account login</h2>
            <form className="form-stack" onSubmit={handleSubmit}>
              {loggedOutMessage ? (
                <p className="inline-message inline-message--neutral" role="status">
                  {loggedOutMessage}
                </p>
              ) : null}
              {feedback ? (
                <p
                  className={`inline-message inline-message--${feedback.tone}`}
                  role={feedback.tone === "error" ? "alert" : "status"}
                >
                  {feedback.message}
                </p>
              ) : null}

              <label className="field-label" htmlFor="username">
                Username
              </label>
              <input
                id="username"
                name="username"
                autoComplete="username"
                className="text-input"
                disabled={isSubmitting}
                onChange={(event) => setUsername(event.target.value)}
                required
                value={username}
              />

              <label className="field-label" htmlFor="password">
                Password
              </label>
              <input
                id="password"
                name="password"
                autoComplete="current-password"
                className="text-input"
                disabled={isSubmitting}
                onChange={(event) => setPassword(event.target.value)}
                required
                type="password"
                value={password}
              />

              <button className="button-link button-link--primary" disabled={isSubmitting} type="submit">
                {isSubmitting ? "Signing in..." : "Sign in"}
              </button>
            </form>
          </section>
        </div>

        <footer className="helper-links" aria-label="Login page secondary actions">
          <Link className="text-link" href="/">
            Return to home
          </Link>
          <Link className="text-link" href="/#institution-selection">
            Institution selection
          </Link>
          <Link className="text-link" href="/login#login-help">
            Contact support
          </Link>
        </footer>
      </section>
    </main>
  );
}

