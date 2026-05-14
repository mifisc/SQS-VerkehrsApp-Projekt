import type { TrafficEvent } from '../components/IncidentMap';

const API_BASE = '/api';

export async function fetchAvailableRoads(): Promise<string[]> {
  const response = await fetch(`${API_BASE}/traffic`);
  if (!response.ok) {
    throw new Error(`Fehler beim Laden der Autobahnen: ${response.status}`);
  }
  const data = await response.json();
  const roads: string[] = [...new Set<string>((data.events ?? []).map((e: { roadId: string }) => e.roadId))];
  return roads.sort();
}

export async function fetchTrafficEvents(roadId?: string): Promise<TrafficEvent[]> {
  const url = roadId ? `${API_BASE}/traffic/${roadId}` : `${API_BASE}/traffic`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Fehler beim Laden der Ereignisse: ${response.status}`);
  }
  const data = await response.json();
  return data.events ?? [];
}
