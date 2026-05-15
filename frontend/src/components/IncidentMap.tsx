import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

// Fix für fehlende Marker-Icons in Leaflet + Vite
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';

L.Icon.Default.mergeOptions({
  iconUrl: markerIcon,
  iconRetinaUrl: markerIcon2x,
  shadowUrl: markerShadow,
});

export interface TrafficEvent {
  id: string;
  roadId: string;
  title: string;
  subtitle: string;
  description: string;
  type: string;
  latitude: number;
  longitude: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
}

interface IncidentMapProps {
  events: TrafficEvent[];
}

export function IncidentMap({ events }: IncidentMapProps) {
  return (
    <div data-testid="incident-map" style={{ height: '500px', width: '100%' }}>
      <MapContainer
        center={[51.1657, 10.4515]}
        zoom={6}
        style={{ height: '100%', width: '100%' }}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {events.map((event) => (
          <Marker key={event.id} position={[event.latitude, event.longitude]}>
            <Popup>
              <strong>{event.title}</strong>
              <br />
              {event.subtitle}
              <br />
              Risiko: {event.riskLevel}
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}
