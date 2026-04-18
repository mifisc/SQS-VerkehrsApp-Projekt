import type { AuthResponse, AvailableRoutesResponse, DashboardResponse, IncidentResponse } from "./types";

async function request<T>(path: string, init?: RequestInit, token?: string): Promise<T> {
  const headers = new Headers(init?.headers);
  headers.set("Content-Type", "application/json");
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(path, { ...init, headers });
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    const message =
      (Array.isArray(body?.details) ? body.details.join(" ") : "") ||
      (typeof body?.error === "string" ? body.error : "") ||
      `Anfrage fehlgeschlagen (${response.status}).`;
    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export function fetchIncidents(roads: string[], allSelected: boolean): Promise<IncidentResponse> {
  const query = allSelected
    ? "?all=true"
    : roads.length > 0
      ? `?roads=${encodeURIComponent(roads.join(","))}`
      : "";
  return request<IncidentResponse>(`/api/public/incidents${query}`);
}

export function fetchAvailableRoutes(): Promise<AvailableRoutesResponse> {
  return request<AvailableRoutesResponse>("/api/public/routes");
}

export function login(username: string, password: string): Promise<AuthResponse> {
  return request<AuthResponse>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password })
  });
}

export function register(username: string, displayName: string, password: string): Promise<AuthResponse> {
  return request<AuthResponse>("/api/auth/register", {
    method: "POST",
    body: JSON.stringify({ username, displayName, password })
  });
}

export function fetchDashboard(token: string): Promise<DashboardResponse> {
  return request<DashboardResponse>("/api/dashboard", undefined, token);
}

export function createRouteWatch(
  token: string,
  payload: { name: string; roads: string[]; notes: string }
): Promise<void> {
  return request<void>(
    "/api/dashboard/routes",
    {
      method: "POST",
      body: JSON.stringify(payload)
    },
    token
  );
}

export function deleteRouteWatch(token: string, routeWatchId: number): Promise<void> {
  return request<void>(
    `/api/dashboard/routes/${routeWatchId}`,
    {
      method: "DELETE"
    },
    token
  );
}
