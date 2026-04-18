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

function riskLabel(score: number): string {
  if (score >= 75) {
    return "hoch";
  }
  if (score >= 45) {
    return "mittel";
  }
  if (score > 0) {
    return "niedrig";
  }
  return "keine Auffaelligkeit";
}

function buildRoadSummary(selectedRoads: string[], allRoads: string[], allSelected: boolean): string {
  if (allRoads.length === 0) {
    return "Autobahnen werden geladen";
  }
  if (allSelected) {
    return `Alle ${allRoads.length} Autobahnen ausgewaehlt`;
  }
  if (selectedRoads.length === 0) {
    return "Keine Autobahn ausgewaehlt";
  }
  if (selectedRoads.length <= 3) {
    return selectedRoads.join(", ");
  }
  return `${selectedRoads.length} von ${allRoads.length} Autobahnen ausgewaehlt`;
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

function toggleRoad(selectedRoads: string[], road: string): string[] {
  return selectedRoads.includes(road)
    ? selectedRoads.filter((selected) => selected !== road)
    : [...selectedRoads, road].sort(compareRoads);
}

function filterRoads(roads: string[], query: string): string[] {
  const normalizedQuery = query.trim().toLowerCase();
  if (!normalizedQuery) {
    return roads;
  }
  return roads.filter((road) => road.toLowerCase().includes(normalizedQuery));
}

function RoadPicker({
  title,
  description,
  roads,
  selectedRoads,
  allSelected,
  isOpen,
  searchValue,
  onToggleOpen,
  onToggleAll,
  onToggleRoad,
  onSearchChange
}: {
  title: string;
  description: string;
  roads: string[];
  selectedRoads: string[];
  allSelected: boolean;
  isOpen: boolean;
  searchValue: string;
  onToggleOpen: () => void;
  onToggleAll: () => void;
  onToggleRoad: (road: string) => void;
  onSearchChange: (value: string) => void;
}) {
  const filteredRoads = filterRoads(roads, searchValue);
  const summary = buildRoadSummary(selectedRoads, roads, allSelected);

  return (
    <section className={`road-picker${isOpen ? " road-picker--open" : ""}`}>
      <button type="button" className="road-picker__toggle" onClick={onToggleOpen}>
        <div>
          <span className="road-picker__label">{title}</span>
          <strong>{summary}</strong>
          <p>{description}</p>
        </div>
        <span className="road-picker__icon" aria-hidden="true">
          {isOpen ? "−" : "+"}
        </span>
      </button>
      {isOpen ? (
        <div className="road-picker__panel">
          <div className="road-picker__toolbar">
            <label>
              Suche
              <input
                type="text"
                value={searchValue}
                onChange={(event) => onSearchChange(event.target.value)}
                placeholder="z. B. A3"
              />
            </label>
            <button type="button" className={`road-option road-option--all${allSelected ? " road-option--active" : ""}`} onClick={onToggleAll}>
              <span>Alle</span>
            </button>
          </div>
          <div className="road-selector">
            {filteredRoads.map((road) => {
              const checked = allSelected || selectedRoads.includes(road);
              return (
                <label key={road} className={`road-option${checked ? " road-option--active" : ""}`}>
                  <input
                    type="checkbox"
                    checked={checked}
                    onChange={() => onToggleRoad(road)}
                  />
                  <span>{road}</span>
                </label>
              );
            })}
          </div>
        </div>
      ) : null}
    </section>
  );
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
  const [incidentResponse, setIncidentResponse] = useState<IncidentResponse | null>(null);
  const [loadingIncidents, setLoadingIncidents] = useState(false);
  const [loadingDashboard, setLoadingDashboard] = useState(false);
  const [submittingRoute, setSubmittingRoute] = useState(false);
  const [submittingAuth, setSubmittingAuth] = useState(false);
  const [pageError, setPageError] = useState<string | null>(null);

  const [publicRoads, setPublicRoads] = useState<string[]>([]);
  const [allPublicRoadsSelected, setAllPublicRoadsSelected] = useState(false);
  const [routeRoads, setRouteRoads] = useState<string[]>([]);
  const [allRouteRoadsSelected, setAllRouteRoadsSelected] = useState(false);
  const [publicRoadPickerOpen, setPublicRoadPickerOpen] = useState(false);
  const [routeRoadPickerOpen, setRouteRoadPickerOpen] = useState(false);
  const [publicRoadSearch, setPublicRoadSearch] = useState("");
  const [routeRoadSearch, setRouteRoadSearch] = useState("");
  const [roadsInitialized, setRoadsInitialized] = useState(false);

  const [routeName, setRouteName] = useState("");
  const [routeNotes, setRouteNotes] = useState("");

  const [authDialogOpen, setAuthDialogOpen] = useState(false);
  const [authMode, setAuthMode] = useState<"login" | "register">("login");
  const [loginUsername, setLoginUsername] = useState("demo_driver");
  const [loginPassword, setLoginPassword] = useState("Demo123!");
  const [registerUsername, setRegisterUsername] = useState("");
  const [registerDisplayName, setRegisterDisplayName] = useState("");
  const [registerPassword, setRegisterPassword] = useState("");

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

        if (!roadsInitialized) {
          setPublicRoads(routes);
          setAllPublicRoadsSelected(true);
          setRoadsInitialized(true);
          await loadIncidents(routes, true);
        }
      } catch (error) {
        setPageError(error instanceof Error ? error.message : "Autobahnen konnten nicht geladen werden.");
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
  }, [session]);

  async function loadIncidents(roads: string[], allSelected: boolean) {
    setLoadingIncidents(true);
    setPageError(null);
    try {
      const response = await fetchIncidents(roads, allSelected);
      setIncidentResponse(response);
    } catch (error) {
      setPageError(error instanceof Error ? error.message : "Meldungen konnten nicht geladen werden.");
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
      setPageError(error instanceof Error ? error.message : "Dashboard konnte nicht geladen werden.");
    } finally {
      setLoadingDashboard(false);
    }
  }

  async function handlePublicSearch() {
    if (!allPublicRoadsSelected && publicRoads.length === 0) {
      setPageError("Bitte waehle mindestens eine Autobahn aus oder nutze die Auswahl Alle.");
      setPublicRoadPickerOpen(true);
      return;
    }
    await loadIncidents(publicRoads, allPublicRoadsSelected);
  }

  async function handleLoginSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmittingAuth(true);
    setPageError(null);

    try {
      const response = await login(loginUsername, loginPassword);
      setSession(response);
      setAuthDialogOpen(false);
    } catch (error) {
      setPageError(error instanceof Error ? error.message : "Login fehlgeschlagen.");
    } finally {
      setSubmittingAuth(false);
    }
  }

  async function handleRegisterSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmittingAuth(true);
    setPageError(null);

    try {
      const response = await register(registerUsername, registerDisplayName, registerPassword);
      setSession(response);
      setAuthDialogOpen(false);
      setAuthMode("login");
    } catch (error) {
      setPageError(error instanceof Error ? error.message : "Registrierung fehlgeschlagen.");
    } finally {
      setSubmittingAuth(false);
    }
  }

  async function handleCreateRoute(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!session) {
      setAuthDialogOpen(true);
      return;
    }

    const selectedRoads = allRouteRoadsSelected ? availableRoads : routeRoads;
    if (selectedRoads.length === 0) {
      setPageError("Bitte waehle mindestens eine Autobahn fuer die beobachtete Route aus.");
      setRouteRoadPickerOpen(true);
      return;
    }

    setSubmittingRoute(true);
    setPageError(null);

    try {
      await createRouteWatch(session.token, {
        name: routeName,
        roads: selectedRoads,
        notes: routeNotes
      });
      setRouteName("");
      setRouteNotes("");
      setRouteRoads([]);
      setAllRouteRoadsSelected(false);
      await loadDashboard(session);
    } catch (error) {
      setPageError(error instanceof Error ? error.message : "Route konnte nicht gespeichert werden.");
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
    } catch (error) {
      setPageError(error instanceof Error ? error.message : "Route konnte nicht entfernt werden.");
    }
  }

  function toggleAllPublicRoads() {
    if (allPublicRoadsSelected) {
      setAllPublicRoadsSelected(false);
      setPublicRoads([]);
      return;
    }

    setAllPublicRoadsSelected(true);
    setPublicRoads(availableRoads);
  }

  function toggleSinglePublicRoad(road: string) {
    if (allPublicRoadsSelected) {
      const nextRoads = availableRoads.filter((value) => value !== road);
      setAllPublicRoadsSelected(false);
      setPublicRoads(nextRoads);
      return;
    }

    const nextRoads = toggleRoad(publicRoads, road);
    if (nextRoads.length === availableRoads.length) {
      setAllPublicRoadsSelected(true);
    }
    setPublicRoads(nextRoads);
  }

  function toggleAllRouteRoads() {
    if (allRouteRoadsSelected) {
      setAllRouteRoadsSelected(false);
      setRouteRoads([]);
      return;
    }

    setAllRouteRoadsSelected(true);
    setRouteRoads(availableRoads);
  }

  function toggleSingleRouteRoad(road: string) {
    if (allRouteRoadsSelected) {
      const nextRoads = availableRoads.filter((value) => value !== road);
      setAllRouteRoadsSelected(false);
      setRouteRoads(nextRoads);
      return;
    }

    const nextRoads = toggleRoad(routeRoads, road);
    if (nextRoads.length === availableRoads.length) {
      setAllRouteRoadsSelected(true);
    }
    setRouteRoads(nextRoads);
  }

  function logout() {
    setSession(null);
    setAuthDialogOpen(false);
  }

  const groupedIncidents = groupIncidentsByRoad(incidentResponse?.incidents ?? []);
  const stats = incidentResponse?.stats;

  return (
    <div className="app-shell">
      <header className="hero hero--compact">
        <div className="hero__content">
          <div className="eyebrow">Autobahn Safety Monitor</div>
          <h1>Gefahren auf deutschen Autobahnen live im Blick.</h1>
          <p className="hero__copy">
            Die Karte zeigt aktuelle Meldungen der Autobahn API. Darunter findest du die Risikoeinschaetzung,
            bevor du im unteren Bereich deine beobachteten Strecken zusammenstellst.
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
            <button type="button" onClick={() => setAuthDialogOpen(true)}>
              Login oder Registrierung
            </button>
          )}
        </div>
      </header>

      {pageError ? <div className="error-banner">{pageError}</div> : null}

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
                <p className="info-note">
                  Nach Autobahn gruppiert, damit du lange Listen schneller ueberblickst.
                </p>
              </div>
              <span className="status-chip">
                {incidentResponse ? `${incidentResponse.incidents.length} Meldungen` : "Wird geladen"}
              </span>
            </div>
            <div className="incident-list incident-list--scroll">
              {groupedIncidents.length === 0 ? (
                <p className="empty-state">Aktuell liegen fuer die gewaehlten Autobahnen keine Meldungen vor.</p>
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
            hint="Kurzfristige Gefahrenhinweise und Stoerungen."
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
                Die Liste bleibt eingeklappt und laedt auf Wunsch alle Autobahnen ohne URL-Limit.
              </p>
            </div>
            <div className="controls-actions">
              <span className="status-chip">
                {allPublicRoadsSelected ? "Filter: Alle Autobahnen" : `${publicRoads.length} Autobahnen aktiv`}
              </span>
              <button type="button" onClick={() => void handlePublicSearch()} disabled={loadingIncidents}>
                {loadingIncidents ? "Suche laeuft..." : "Meldungen laden"}
              </button>
            </div>
          </div>
          <RoadPicker
            title="Oeffentliche Suche"
            description="Waehle einzelne Autobahnen aus oder blende die gesamte Liste mit einem Klick ein."
            roads={availableRoads}
            selectedRoads={publicRoads}
            allSelected={allPublicRoadsSelected}
            isOpen={publicRoadPickerOpen}
            searchValue={publicRoadSearch}
            onToggleOpen={() => setPublicRoadPickerOpen((current) => !current)}
            onToggleAll={toggleAllPublicRoads}
            onToggleRoad={toggleSinglePublicRoad}
            onSearchChange={setPublicRoadSearch}
          />
        </section>

        <section className="dashboard-grid">
          <article className="panel">
            <div className="panel__header">
              <div>
                <h2>Persoenliches Dashboard</h2>
                <p className="info-note">
                  Beobachtete Strecken mit eigenem Routenrisiko und aktuellen Highlights.
                </p>
              </div>
              {!session ? (
                <button type="button" className="ghost-button" onClick={() => setAuthDialogOpen(true)}>
                  Zum Login
                </button>
              ) : null}
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
                  <p className="empty-state">Noch keine beobachteten Strecken gespeichert.</p>
                )}
              </div>
            ) : (
              <div className="panel panel--nested panel--cta">
                <h3>Login nur bei Bedarf</h3>
                <p>
                  Der geschuetzte Bereich ist bewusst versteckt. Oeffne den Dialog erst dann, wenn du Strecken
                  speichern oder dein Demo-Dashboard testen willst.
                </p>
                <button type="button" onClick={() => setAuthDialogOpen(true)}>
                  Dialog oeffnen
                </button>
              </div>
            )}
          </article>

          <article className="panel">
            <div className="panel__header">
              <div>
                <h2>Route beobachten</h2>
                <p className="info-note">
                  Speichere eine Strecke fuer den geschuetzten Endpunkt. Das Formular nutzt dieselbe Autobahnliste.
                </p>
              </div>
            </div>
            <form className="route-form" onSubmit={handleCreateRoute}>
              <label>
                Name der Route
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
                Kurznotiz
                <textarea
                  value={routeNotes}
                  onChange={(event) => setRouteNotes(event.target.value)}
                  placeholder="optional"
                  maxLength={255}
                />
              </label>
              <RoadPicker
                title="Autobahnen fuer diese Route"
                description="Auch hier bleibt die lange Liste eingeklappt und durchsuchbar."
                roads={availableRoads}
                selectedRoads={routeRoads}
                allSelected={allRouteRoadsSelected}
                isOpen={routeRoadPickerOpen}
                searchValue={routeRoadSearch}
                onToggleOpen={() => setRouteRoadPickerOpen((current) => !current)}
                onToggleAll={toggleAllRouteRoads}
                onToggleRoad={toggleSingleRouteRoad}
                onSearchChange={setRouteRoadSearch}
              />
              <button type="submit" disabled={submittingRoute}>
                {submittingRoute ? "Speichere Route..." : "Route speichern"}
              </button>
            </form>
          </article>
        </section>
      </main>

      {authDialogOpen ? (
        <div className="modal-backdrop" role="presentation" onClick={() => setAuthDialogOpen(false)}>
          <div className="modal-card" role="dialog" aria-modal="true" onClick={(event) => event.stopPropagation()}>
            <div className="panel__header">
              <div>
                <h2>Login und Registrierung</h2>
                <p className="info-note">Nur fuer den geschuetzten Bereich und das persoenliche Dashboard.</p>
              </div>
              <button type="button" className="ghost-button" onClick={() => setAuthDialogOpen(false)}>
                Schliessen
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
                  {submittingAuth ? "Login laeuft..." : "Einloggen"}
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
                  {submittingAuth ? "Registrierung laeuft..." : "Konto anlegen"}
                </button>
              </form>
            )}
          </div>
        </div>
      ) : null}
    </div>
  );
}
