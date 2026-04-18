import { CircleMarker, MapContainer, Popup, TileLayer } from "react-leaflet";
import type { Incident } from "../lib/types";
import "leaflet/dist/leaflet.css";

const germanyCenter: [number, number] = [51.1657, 10.4515];
const germanyBounds: [[number, number], [number, number]] = [
  [46.0, 5.3],
  [55.5, 15.8]
];

function colorForIncident(category: string): string {
  switch (category) {
    case "CLOSURE":
      return "#d1495b";
    case "WARNING":
      return "#edae49";
    default:
      return "#00798c";
  }
}

export function IncidentMap({ incidents }: { incidents: Incident[] }) {
  return (
    <MapContainer
      center={germanyCenter}
      zoom={6}
      minZoom={5}
      maxBounds={germanyBounds}
      maxBoundsViscosity={1}
      scrollWheelZoom
      className="map-panel"
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {incidents.map((incident) => (
        <CircleMarker
          key={incident.id}
          center={[incident.latitude, incident.longitude]}
          radius={Math.min(18, 6 + Math.round(incident.riskWeight / 12))}
          pathOptions={{
            color: colorForIncident(incident.category),
            fillColor: colorForIncident(incident.category),
            fillOpacity: 0.5
          }}
        >
          <Popup>
            <strong>{incident.title}</strong>
            <div>{incident.subtitle}</div>
            <div>{incident.categoryLabel}</div>
          </Popup>
        </CircleMarker>
      ))}
    </MapContainer>
  );
}
