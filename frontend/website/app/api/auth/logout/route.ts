import { NextResponse } from "next/server";
import { AUTH_TOKEN_COOKIE } from "@/lib/auth";

export async function POST() {
  const response = NextResponse.json({ loggedOut: true }, { status: 200 });
  response.cookies.delete(AUTH_TOKEN_COOKIE);
  return response;
}

