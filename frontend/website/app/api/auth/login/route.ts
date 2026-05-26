import { NextResponse } from "next/server";
import { buildBackendUrl } from "@/lib/backend";
import { AUTH_TOKEN_COOKIE } from "@/lib/auth";

type LoginPayload = {
  username?: string;
  password?: string;
};

type BackendLoginResponse = {
  accessToken: string;
  tokenType: string;
  expiresAt: string;
};

function isValidPayload(payload: LoginPayload): payload is Required<LoginPayload> {
  return Boolean(payload.username?.trim() && payload.password?.trim());
}

function buildCookieExpiry(expiresAt: string): Date | undefined {
  const parsed = new Date(expiresAt);
  return Number.isNaN(parsed.valueOf()) ? undefined : parsed;
}

export async function POST(request: Request) {
  let payload: LoginPayload;

  try {
    payload = (await request.json()) as LoginPayload;
  } catch {
    return NextResponse.json({ message: "A valid JSON login payload is required." }, { status: 400 });
  }

  if (!isValidPayload(payload)) {
    return NextResponse.json(
      { message: "Both username and password are required." },
      { status: 400 },
    );
  }

  try {
    const backendResponse = await fetch(buildBackendUrl("/api/v1/identity/auth/login"), {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      cache: "no-store",
      body: JSON.stringify({
        username: payload.username.trim(),
        password: payload.password,
      }),
    });

    if (backendResponse.status === 401) {
      return NextResponse.json({ message: "The username or password is incorrect." }, { status: 401 });
    }

    if (!backendResponse.ok) {
      return NextResponse.json(
        { message: "The identity service could not complete sign-in." },
        { status: backendResponse.status },
      );
    }

    const loginResponse = (await backendResponse.json()) as BackendLoginResponse;
    const response = NextResponse.json(
      {
        expiresAt: loginResponse.expiresAt,
        tokenType: loginResponse.tokenType,
      },
      { status: 200 },
    );

    response.cookies.set({
      name: AUTH_TOKEN_COOKIE,
      value: loginResponse.accessToken,
      httpOnly: true,
      sameSite: "lax",
      secure: process.env.NODE_ENV === "production",
      path: "/",
      expires: buildCookieExpiry(loginResponse.expiresAt),
    });

    return response;
  } catch {
    return NextResponse.json(
      { message: "Unable to reach the identity service right now." },
      { status: 503 },
    );
  }
}

