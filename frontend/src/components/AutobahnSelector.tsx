import { useEffect, useState } from 'react';
import { fetchAvailableRoads } from '../services/trafficService';

interface AutobahnSelectorProps {
  onSelect: (roadId: string) => void;
}

export function AutobahnSelector({ onSelect }: AutobahnSelectorProps) {
  const [roads, setRoads] = useState<string[]>([]);
  const [selected, setSelected] = useState('');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchAvailableRoads()
      .then(setRoads)
      .catch(() => setError('Autobahnen konnten nicht geladen werden.'));
  }, []);

  function handleChange(event: React.ChangeEvent<HTMLSelectElement>) {
    setSelected(event.target.value);
    onSelect(event.target.value);
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <select
      data-testid="autobahn-selector"
      value={selected}
      onChange={handleChange}
    >
      <option value="">Autobahn auswählen...</option>
      {roads.map((road) => (
        <option key={road} value={road}>
          {road}
        </option>
      ))}
    </select>
  );
}
