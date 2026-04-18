import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { IncidentMap } from "./components/IncidentMap";
import { RouteWatchCard } from "./components/RouteWatchCard";
import { StatCard } from "./components/StatCard";
import {
  createRouteWatch,
  deleteRouteWatch,
  fetchAvailableRoutes,
  fetchDashboard,
  fetchIncidents,
  login,
  register
} from "./lib/api";
import type { AuthResponse, DashboardResponse, Incident, IncidentResponse } from "./lib/types";
import "./styles.css";

const SESSION_STORAGE_KEY = "autobahn-safety-monitor-session";
const MAX_SELECTED_ROADS = 15;

type Toast = {
  id: number;
  message: string;
};

function compareRoads(left: string, right: string): number {
  const pattern = /^A(\d+)([A-Z]*)$/i;
  const leftMatch = left.match(pattern);
  const rightMatch = right.match(pattern);

  if (!leftMatch || !rightMatch) {
    return left.localeCompare(right, "de");
  }

  const numberDifference = Number(leftMatch[1]) - Number(rightMatch[1]);
  if (numberDifference !== 0) {
    return numberDifference;
  }

  return leftMatch[2].localeCompare(rightMatch[2], "de");
}

function pickRandomRoads(roads: string[], count: number): string[] {
  const shuffled = [...roads];
  for (let index = shuffled.length - 1; index > 0; index -= 1) {
    const randomIndex = Math.floor(Math.random() * (index + 1));
    [shuffled[index], shuffled[randomIndex]] = [shuffled[randomIndex], shuffled[index]];
  }
  return shuffled.slice(0, Math.min(count, roads.length)).sort(compareRoads);
}

function formatTimestamp(value: string | null | undefined): string {
  if (!value) {
    return "Zeitpunkt unbekannt";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "Zeitpunkt unbekannt";
  }

  return new Intl.DateTimeFormat("de-DE", {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(date);
}

function groupIncidentsByRoad(incidents: Incident[]): Array<{ roadId: string; incidents: Incident[] }> {
  const groups = new Map<string, Incident[]>();

  for (const incident of incidents) {
    const existing = groups.get(incident.roadId) ?? [];
    existing.push(incident);
    groups.set(incident.roadId, existing);
  }

  return Array.from(groups.entries())
    .sort(([left], [right]) => compareRoads(left, right))
    .map(([roadId, roadIncidents]) => ({ roadId, incidents: roadIncidents }));
}

function filterRoads(roads: string[], query: string): string[] {
  const normalizedQuery = query.trim().toLowerCase();
  if (!normalizedQuery) {
    return roads;
  }
  return roads.filter((road) => road.toLowerCase().includes(normalizedQuery));
}

function selectionSummary(selectedRoads: string[], allRoads: string[]): string {
  if (allRoads.length === 0) {
    return "Autobahnen werden geladen";
  }
  if (selectedRoads.length === 0) {
    return "Keine Autobahn ausgewählt";
  }
  if (selectedRoads.length === MAX_SELECTED_ROADS) {
    return `${MAX_SELECTED_ROADS} ausgewählt (Maximum)`;
  }
  if (selectedRoads.length <= 3) {
    return selectedRoads.join(", ");
  }
  return `${selectedRoads.length} ausgewählt`;
}

function IncidentCard({ incident }: { incident: Incident }) {
  return (
    <article className="incident-card">
      <h3>{incident.title}</h3>
      <p className="incident-card__subtitle">{incident.subtitle}</p>
      <div className="incident-card__meta">
        <span>{incident.categoryLabel}</span>
        <span>{incident.blocked ? "Blockiert" : "Befahrbar"}</span>
        <span>Risiko {incident.riskWeight}</span>
        <span>{formatTimestamp(incident.startTimestamp)}</span>
      </div>
      {incident.description.length > 0 ? (
        <ul>
          {incident.description.map((item) => (
            <li key={`${incident.id}-${item}`}>{item}</li>
          ))}
        </ul>
      ) : null}
    </article>
  );
}

export default function App() {
  const [session, setSession] = useState<AuthResponse | null>(null);
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);
  const [availableRoads, setAvailableRoads] = useState<string[]>([]);
  const [selectedRoads, setSelectedRoads] = useState<string[]>([]);
  const [roadSearch, setRoadSearch] = useState("");
  const [roadPickerOpen, setRoadPickerOpen] = useState(false);
  const [incidentResponse, setIncidentResponse] = useState<IncidentResponse | null>(null);
  const [loadingIncidents, setLoadingIncidents] = useState(false);
  const [loadingDashboard, setLoadingDashboard] = useState(false);
  const [submittingRoute, setSubmittingRoute] = useState(false);
  const [submittingAuth, setSubmittingAuth] = useState(false);
  const [toasts, setToasts] = useState<Toast[]>([]);
  const [toastCounter, setToastCounter] = useState(0);

  const [routeName, setRouteName] = useState("");
  const [routeNotes, setRouteNotes] = useState("");

  const [authDialogOpen, setAuthDialogOpen] = useState(false);
  const [authMode, setAuthMode] = useState<"login" | "register">("login");
  const [loginUsername, setLoginUsername] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [registerUsername, setRegisterUsername] = useState("");
  const [registerDisplayName, setRegisterDisplayName] = useState("");
  const [registerPassword, setRegisterPassword] = useState("");

  function showToast(message: string) {
    const id = toastCounter + 1;
    setToastCounter(id);
    setToasts((current) => [...current, { id, message }]);
    window.setTimeout(() => {
      setToasts((current) => current.filter((toast) => toast.id !== id));
    }, 4800);
  }

  function openAuthDialog() {
    setAuthMode("login");
    setLoginUsername("");
    setLoginPassword("");
    setRegisterUsername("");
    setRegisterDisplayName("");
    setRegisterPassword("");
    setAuthDialogOpen(true);
  }

  useEffect(() => {
    const storedSession = localStorage.getItem(SESSION_STORAGE_KEY);
    if (storedSession) {
      try {
        setSession(JSON.parse(storedSession) as AuthResponse);
      } catch {
        localStorage.removeItem(SESSION_STORAGE_KEY);
      }
    }
  }, []);

  useEffect(() => {
    void (async () => {
      try {
        const routes = (await fetchAvailableRoutes()).sort(compareRoads);
        setAvailableRoads(routes);
        const initialRoads = pickRandomRoads(routes, 2);
        setSelectedRoads(initialRoads);
        await loadIncidents(initialRoads);
      } catch (error) {
        showToast(error instanceof Error ? error.message : "Autobahnen konnten nicht geladen werden.");
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!session) {
      setDashboard(null);
      localStorage.removeItem(SESSION_STORAGE_KEY);
      return;
    }

    localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session));
    void loadDashboard(session);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  async function loadIncidents(roads: string[]) {
    if (roads.length === 0) {
      showToast("Bitte wähle mindestens eine Autobahn aus.");
      return;
    }

    setLoadingIncidents(true);
    try {
      const response = await fetchIncidents(roads);
      setIncidentResponse(response);
    } catch (error) {
      showToast(error instanceof Error ? error.message : "Meldungen konnten nicht geladen werden.");
    } finally {
      setLoadingIncidents(false);
    }
  }

  async function loadDashboard(currentSession: AuthResponse) {
    setLoadingDashboard(true);
    try {
      const response = await fetchDashboard(currentSession.token);
      setDashboard(response);
    } catch (error) {
      setSession(null);
      showToast(error instanceof Error ? error.message : "Dashboard konnte nicht geladen werden.");
    } finally {
      setLoadingDashboard(false);
    }
  }

  function toggleRoad(road: string) {
    setSelectedRoads((current) => {
      if (current.includes(road)) {
        return current.filter((selected) => selected !== road);
      }
      if (current.length >= MAX_SELECTED_ROADS) {
        showToast(`Maximal ${MAX_SELECTED_ROADS} Autobahnen gleichzeitig auswählen.`);
        return current;
      }
      return [...current, road].sort(compareRoads);
    });
  }

  function selectMaxRoads() {
    const limitedRoads = availableRoads.slice(0, MAX_SELECTED_ROADS);
    setSelectedRoads(limitedRoads);
    showToast(`Es wurden ${MAX_SELECTED_ROADS} Autobahnen ausgewählt.`);
  }

  async function handleLoginSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmittingAuth(true);

    try {
      const response = await login(loginUsername, loginPassword);
      setSession(response);
      setAuthDialogOpen(false);
    } catch (error) {
      showToast(error instanceof Error ? error.message : "Login fehlgeschlagen.");
    } finally {
      setSubmittingAuth(false);
    }
  }

  async function handleRegisterSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmittingAuth(true);

    try {
      const response = await register(registerUsername, registerDisplayName, registerPassword);
      setSession(response);
      setAuthDialogOpen(false);
      setAuthMode("login");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "Registrierung fehlgeschlagen.");
    } finally {
      setSubmittingAuth(false);
    }
  }

  async function handleCreateRoute(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!session) {
      openAuthDialog();
      showToast("Bitte zuerst einloggen, um ein Preset zu speichern.");
      return;
    }

    if (selectedRoads.length === 0) {
      showToast("Bitte wähle mindestens eine Autobahn aus.");
      setRoadPickerOpen(true);
      return;
    }

    setSubmittingRoute(true);
    try {
      await createRouteWatch(session.token, {
        name: routeName,
        roads: selectedRoads,
        notes: routeNotes
      });
      setRouteName("");
      setRouteNotes("");
      await loadDashboard(session);
      showToast("Preset wurde gespeichert.");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "Preset konnte nicht gespeichert werden.");
    } finally {
      setSubmittingRoute(false);
    }
  }

  async function handleDeleteRoute(routeWatchId: number) {
    if (!session) {
      return;
    }

    try {
      await deleteRouteWatch(session.token, routeWatchId);
      await loadDashboard(session);
      showToast("Preset wurde entfernt.");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "Preset konnte nicht entfernt werden.");
    }
  }

  function logout() {
    setSession(null);
    setAuthDialogOpen(false);
  }

  const groupedIncidents = groupIncidentsByRoad(incidentResponse?.incidents ?? []);
  const stats = incidentResponse?.stats;
  const filteredRoads = filterRoads(availableRoads, roadSearch);

  return (
    <div className="app-shell">
      <header className="hero hero--compact">
        <div className="hero__content">
          <div className="eyebrow">Autobahn Safety Monitor</div>
          <h1>Gefahren auf deutschen Autobahnen live im Blick</h1>
          <p className="hero__copy">
            Eine Karte für aktuelle Meldungen und darunter eine große, zentrale Autobahnauswahl für Suche und Presets.
          </p>
        </div>
        <div className="hero__actions">
          {session ? (
            <div className="user-badge">
              <strong>{session.displayName}</strong>
              <span>{session.demoAccount ? "Demo-Konto" : session.username}</span>
              <button type="button" className="ghost-button" onClick={logout}>
                Abmelden
              </button>
            </div>
          ) : (
            <button type="button" onClick={openAuthDialog}>
              Login oder Registrierung
            </button>
          )}
        </div>
      </header>

      <main className="content-flow">
        <section className="main-grid main-grid--top">
          <article className="panel">
            <div className="panel__header">
              <div>
                <h2>Live-Karte</h2>
                <p className="info-note">
                  Fokus auf Deutschland, Datenquelle {incidentResponse?.source ?? "wird geladen"}.
                </p>
              </div>
              <span className="status-chip">
                {loadingIncidents ? "Aktualisiere Meldungen..." : `Stand ${formatTimestamp(incidentResponse?.generatedAt)}`}
              </span>
            </div>
            <IncidentMap incidents={incidentResponse?.incidents ?? []} />
          </article>

          <article className="panel panel--scroll">
            <div className="panel__header">
              <div>
                <h2>Meldungen</h2>
                <p className="info-note">Nach Autobahn sortiert und bei vielen Einträgen scrollbar.</p>
              </div>
              <span className="status-chip">
                {incidentResponse ? `${incidentResponse.incidents.length} Meldungen` : "Wird geladen"}
              </span>
            </div>
            <div className="incident-list incident-list--scroll">
              {groupedIncidents.length === 0 ? (
                <p className="empty-state">Aktuell liegen für die gewählten Autobahnen keine Meldungen vor.</p>
              ) : (
                groupedIncidents.map((group) => (
                  <section key={group.roadId} className="incident-road-group">
                    <div className="incident-road-group__header">
                      <h3>{group.roadId}</h3>
                      <span>{group.incidents.length} Meldungen</span>
                    </div>
                    <div className="incident-group-grid">
                      {group.incidents.map((incident) => (
                        <IncidentCard key={incident.id} incident={incident} />
                      ))}
                    </div>
                  </section>
                ))
              )}
            </div>
          </article>
        </section>

        <section className="stats-grid">
          <StatCard
            label="Gesamtmeldungen"
            value={stats?.total ?? 0}
            tone="neutral"
            hint="Alle live geladenen Warnungen, Baustellen und Sperrungen."
          />
          <StatCard
            label="Warnungen"
            value={stats?.warnings ?? 0}
            tone="warning"
            hint="Kurzfristige Gefahrenhinweise und Störungen."
          />
          <StatCard
            label="Sperrungen"
            value={stats?.closures ?? 0}
            tone="danger"
            hint="Abschnitte mit Voll- oder Teilsperrung."
          />
          <StatCard
            label="Verkehrsrisiko"
            value={`${stats?.riskScore ?? 0}/100`}
            tone={(stats?.riskScore ?? 0) >= 75 ? "danger" : (stats?.riskScore ?? 0) >= 45 ? "warning" : "neutral"}
            hint="Score aus Anzahl, Schwere und blockierten Abschnitten."
          />
        </section>

        <section className="panel panel--controls">
          <div className="panel__header">
            <div>
              <h2>Autobahnauswahl</h2>
              <p className="info-note">
                Eine zentrale Auswahl für Suche und Presets. Beim Start werden 2 zufällige Autobahnen geladen.
              </p>
            </div>
            <div className="controls-actions">
              <span className="status-chip">
                {selectionSummary(selectedRoads, availableRoads)}
              </span>
              <button type="button" className="ghost-button" onClick={selectMaxRoads}>
                Alle (max {MAX_SELECTED_ROADS})
              </button>
              <button type="button" onClick={() => void loadIncidents(selectedRoads)} disabled={loadingIncidents}>
                {loadingIncidents ? "Suche läuft..." : "Meldungen laden"}
              </button>
            </div>
          </div>

          <section className={`road-picker${roadPickerOpen ? " road-picker--open" : ""}`}>
            <button type="button" className="road-picker__toggle" onClick={() => setRoadPickerOpen((current) => !current)}>
              <div>
                <span className="road-picker__label">Autobahnen</span>
                <strong>{selectionSummary(selectedRoads, availableRoads)}</strong>
                <p>Maximum {MAX_SELECTED_ROADS} Autobahnen gleichzeitig, um Timeouts zu vermeiden.</p>
              </div>
              <span className="road-picker__icon" aria-hidden="true">
                {roadPickerOpen ? "−" : "+"}
              </span>
            </button>

            {roadPickerOpen ? (
              <div className="road-picker__panel">
                <label>
                  Suche
                  <input
                    type="text"
                    value={roadSearch}
                    onChange={(event) => setRoadSearch(event.target.value)}
                    placeholder="z. B. A3"
                  />
                </label>
                <div className="road-selector">
                  {filteredRoads.map((road) => (
                    <label key={road} className={`road-option${selectedRoads.includes(road) ? " road-option--active" : ""}`}>
                      <input
                        type="checkbox"
                        checked={selectedRoads.includes(road)}
                        onChange={() => toggleRoad(road)}
                      />
                      <span>{road}</span>
                    </label>
                  ))}
                </div>
              </div>
            ) : null}
          </section>

          <form className="route-form route-form--inline" onSubmit={handleCreateRoute}>
            <label>
              Preset-Name
              <input
                type="text"
                value={routeName}
                onChange={(event) => setRouteName(event.target.value)}
                placeholder="z. B. Pendelstrecke Ost"
                maxLength={80}
                required
              />
            </label>
            <label>
              Notiz
              <textarea
                value={routeNotes}
                onChange={(event) => setRouteNotes(event.target.value)}
                placeholder="optional"
                maxLength={255}
              />
            </label>
            <button type="submit" disabled={submittingRoute}>
              {submittingRoute ? "Speichere Preset..." : "Preset speichern"}
            </button>
          </form>
        </section>

        <section className="panel">
          <div className="panel__header">
            <div>
              <h2>Persönliches Dashboard</h2>
              <p className="info-note">Gespeicherte Presets mit eigenem Routenrisiko und aktuellen Highlights.</p>
            </div>
          </div>

          {session ? (
            <div className="dashboard-space">
              <p className="auth-hint">
                Angemeldet als <strong>{session.displayName}</strong>
                {session.demoAccount ? " (Demo-Konto)." : "."}
              </p>
              {loadingDashboard ? (
                <p className="empty-state">Dashboard wird geladen...</p>
              ) : dashboard && dashboard.routeWatches.length > 0 ? (
                <div className="route-grid">
                  {dashboard.routeWatches.map((routeWatch) => (
                    <RouteWatchCard key={routeWatch.id} routeWatch={routeWatch} onDelete={handleDeleteRoute} />
                  ))}
                </div>
              ) : (
                <p className="empty-state">Noch keine Presets gespeichert.</p>
              )}
            </div>
          ) : (
            <p className="empty-state">Nicht angemeldet. Für das Dashboard bitte oben rechts einloggen.</p>
          )}
        </section>
      </main>

      {authDialogOpen ? (
        <div className="modal-backdrop" role="presentation" onClick={() => setAuthDialogOpen(false)}>
          <div className="modal-card" role="dialog" aria-modal="true" onClick={(event) => event.stopPropagation()}>
            <div className="panel__header">
              <div>
                <h2>Login und Registrierung</h2>
                <p className="info-note">Nur für den geschützten Bereich und das persönliche Dashboard.</p>
              </div>
              <button type="button" className="ghost-button" onClick={() => setAuthDialogOpen(false)}>
                Schließen
              </button>
            </div>

            <div className="auth-toggle">
              <button
                type="button"
                className={`tab-button${authMode === "login" ? " tab-button--active" : ""}`}
                onClick={() => setAuthMode("login")}
              >
                Login
              </button>
              <button
                type="button"
                className={`tab-button${authMode === "register" ? " tab-button--active" : ""}`}
                onClick={() => setAuthMode("register")}
              >
                Registrierung
              </button>
            </div>

            {authMode === "login" ? (
              <form className="auth-form" onSubmit={handleLoginSubmit}>
                <p className="auth-hint">Demo: `demo_driver` mit Passwort `Demo123!`</p>
                <label>
                  Benutzername
                  <input
                    type="text"
                    value={loginUsername}
                    onChange={(event) => setLoginUsername(event.target.value)}
                    required
                  />
                </label>
                <label>
                  Passwort
                  <input
                    type="password"
                    value={loginPassword}
                    onChange={(event) => setLoginPassword(event.target.value)}
                    required
                  />
                </label>
                <button type="submit" disabled={submittingAuth}>
                  {submittingAuth ? "Login läuft..." : "Einloggen"}
                </button>
              </form>
            ) : (
              <form className="auth-form" onSubmit={handleRegisterSubmit}>
                <label>
                  Benutzername
                  <input
                    type="text"
                    value={registerUsername}
                    onChange={(event) => setRegisterUsername(event.target.value)}
                    required
                  />
                </label>
                <label>
                  Anzeigename
                  <input
                    type="text"
                    value={registerDisplayName}
                    onChange={(event) => setRegisterDisplayName(event.target.value)}
                  />
                </label>
                <label>
                  Passwort
                  <input
                    type="password"
                    value={registerPassword}
                    onChange={(event) => setRegisterPassword(event.target.value)}
                    required
                  />
                </label>
                <button type="submit" disabled={submittingAuth}>
                  {submittingAuth ? "Registrierung läuft..." : "Konto anlegen"}
                </button>
              </form>
            )}
          </div>
        </div>
      ) : null}

      <div className="toast-stack" aria-live="polite">
        {toasts.map((toast) => (
          <div key={toast.id} className="toast">
            <span>{toast.message}</span>
            <button
              type="button"
              className="toast__close"
              onClick={() => setToasts((current) => current.filter((item) => item.id !== toast.id))}
            >
              ×
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
