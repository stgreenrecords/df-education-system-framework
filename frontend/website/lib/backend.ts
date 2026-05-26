import "server-only";

const DEFAULT_BACKEND_BASE_URL = "http://127.0.0.1:8080";

function ensureTrailingSlash(value: string): string {
  return value.endsWith("/") ? value : `${value}/`;
}

export function getBackendBaseUrl(): string {
  return process.env["EDUCATION_API_BASE_URL"]?.trim() || DEFAULT_BACKEND_BASE_URL;
}

export function buildBackendUrl(path: string): string {
  return new URL(path.replace(/^\//, ""), ensureTrailingSlash(getBackendBaseUrl())).toString();
}

