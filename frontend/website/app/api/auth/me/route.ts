import { cookies } from "next/headers";
import { NextResponse } from "next/server";
import { AUTH_TOKEN_COOKIE } from "@/lib/auth";
import { buildBackendUrl } from "@/lib/backend";

export async function GET() {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get(AUTH_TOKEN_COOKIE)?.value;

  if (!accessToken) {
    return NextResponse.json({ message: "Authentication required." }, { status: 401 });
  }

  try {
    const backendResponse = await fetch(buildBackendUrl("/api/v1/identity/me"), {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
      cache: "no-store",
    });

    if (backendResponse.status === 401) {
      const response = NextResponse.json({ message: "Authentication required." }, { status: 401 });
      response.cookies.delete(AUTH_TOKEN_COOKIE);
      return response;
    }

    if (!backendResponse.ok) {
      return NextResponse.json(
        { message: "Unable to resolve the current user right now." },
        { status: backendResponse.status },
      );
    }

    const currentUser = await backendResponse.json();
    return NextResponse.json(currentUser, { status: 200 });
  } catch {
    return NextResponse.json(
      { message: "Unable to reach the identity service right now." },
      { status: 503 },
    );
  }
}

