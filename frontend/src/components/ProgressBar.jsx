export default function ProgressBar({ percent, label }) {
  const value = Math.max(0, Math.min(100, percent));
  return (
    <div className="progress">
      <div className="progress-track">
        <div className={`progress-fill ${value === 100 ? 'done' : ''}`} style={{ width: `${value}%` }} />
      </div>
      <span className="progress-label">{label ?? `${value}%`}</span>
    </div>
  );
}
