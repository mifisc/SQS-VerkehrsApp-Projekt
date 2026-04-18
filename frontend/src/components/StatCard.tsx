export function StatCard({
  label,
  value,
  tone,
  hint
}: {
  label: string;
  value: string | number;
  tone: "neutral" | "warning" | "danger";
  hint?: string;
}) {
  return (
    <article className={`stat-card stat-card--${tone}`}>
      <span>{label}</span>
      <strong>{value}</strong>
      {hint ? <small>{hint}</small> : null}
    </article>
  );
}
