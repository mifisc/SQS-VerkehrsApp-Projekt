import { useState, useEffect } from 'react';
import { AutobahnSelector } from './components/AutobahnSelector';
import { IncidentMap, type TrafficEvent } from './components/IncidentMap';
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
    </main>
  );
}

export default App;