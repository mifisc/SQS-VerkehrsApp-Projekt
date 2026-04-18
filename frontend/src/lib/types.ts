export type DataSource = "LIVE" | "CACHE" | "MIXED";

export interface Incident {
  id: string;
  roadId: string;
  category: string;
  categoryLabel: string;
  title: string;
  subtitle: string;
  description: string[];
  latitude: number;
  longitude: number;
  blocked: boolean;
  future: boolean;
  startTimestamp: string | null;
  riskWeight: number;
  source: DataSource;
}

export interface IncidentStats {
  total: number;
  warnings: number;
  roadworks: number;
  closures: number;
  blocked: number;
  riskScore: number;
}

export interface IncidentResponse {
  roads: string[];
  incidents: Incident[];
  stats: IncidentStats;
  source: DataSource;
  generatedAt: string;
}

export type AvailableRoutesResponse = string[];

export interface AuthResponse {
  token: string;
  username: string;
  displayName: string;
  demoAccount: boolean;
  expiresAt: string;
}

export interface RouteWatch {
  id: number;
  name: string;
  roads: string[];
  notes: string | null;
  demoData: boolean;
  riskScore: number;
  liveIncidents: number;
  source: DataSource;
  refreshedAt: string;
  highlights: string[];
}

export interface DashboardResponse {
  username: string;
  displayName: string;
  demoAccount: boolean;
  routeWatches: RouteWatch[];
}
