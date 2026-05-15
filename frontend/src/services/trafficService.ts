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
