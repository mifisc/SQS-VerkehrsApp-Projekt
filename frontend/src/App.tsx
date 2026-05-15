import { useState, useEffect } from 'react';
import { AutobahnSelector } from './components/AutobahnSelector';
import { IncidentMap, type TrafficEvent } from './components/IncidentMap';
import { RiskBadge } from './components/RiskBadge';
import { fetchTrafficEvents } from './services/trafficService';

function App() {
  const [events, setEvents] = useState<TrafficEvent[]>([]);

  useEffect(() => {
    fetchTrafficEvents().then(setEvents).catch(console.error);
  }, []);

  function handleRoadSelect(roadId: string) {
    fetchTrafficEvents(roadId).then(setEvents).catch(console.error);
  }

  return (
    <main>
      <h1>Autobahn Safety Monitor</h1>
      <AutobahnSelector onSelect={handleRoadSelect} />
      <IncidentMap events={events} />
      <ul>
        {events.map((event) => (
          <li key={event.id}>
            <strong>{event.roadId}</strong> — {event.title}
            <RiskBadge riskLevel={event.riskLevel} />
          </li>
        ))}
      </ul>
    </main>
  );
}

export default App;