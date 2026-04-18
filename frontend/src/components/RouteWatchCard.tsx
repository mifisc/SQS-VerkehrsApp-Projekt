import type { RouteWatch } from "../lib/types";

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
  return "keine Auffälligkeit";
}

export function RouteWatchCard({
  routeWatch,
  onDelete
}: {
  routeWatch: RouteWatch;
  onDelete: (id: number) => void;
}) {
  return (
    <article className="route-card">
      <div className="route-card__header">
        <div>
          <h4>{routeWatch.name}</h4>
          <p>{routeWatch.roads.join(", ")}</p>
        </div>
        <button type="button" className="ghost-button" onClick={() => onDelete(routeWatch.id)}>
          Entfernen
        </button>
      </div>
      {routeWatch.notes ? <p className="route-card__notes">{routeWatch.notes}</p> : null}
      <div className="route-card__stats">
        <span>Routenrisiko: {routeWatch.riskScore}/100 ({riskLabel(routeWatch.riskScore)})</span>
        <span>Aktive Meldungen: {routeWatch.liveIncidents}</span>
        <span>Datenquelle: {routeWatch.source}</span>
      </div>
      <ul className="route-card__highlights">
        {routeWatch.highlights.map((highlight) => (
          <li key={highlight}>{highlight}</li>
        ))}
      </ul>
      {routeWatch.demoData ? <small>Enthält markierte Demo-Daten.</small> : null}
    </article>
  );
}
