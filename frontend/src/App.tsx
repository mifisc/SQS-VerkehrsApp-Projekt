import { AutobahnSelector } from './components/AutobahnSelector';

function App() {
  function handleRoadSelect(roadId: string) {
    console.log('Ausgewählte Autobahn:', roadId);
  }

  return (
    <main>
      <h1>Autobahn Safety Monitor</h1>
      <AutobahnSelector onSelect={handleRoadSelect} />
    </main>
  );
}

export default App;