export default function StatCard({ icon, label, value, color = '#7c3aed' }) {
  return (
    <div className="card stat-card" style={{ '--stat-color': color }}>
      <span className="stat-icon">{icon}</span>
      <div>
        <div className="stat-value">{value}</div>
        <div className="muted">{label}</div>
      </div>
    </div>
  );
}
