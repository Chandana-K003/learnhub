/** Two-panel layout for the login and register pages: a colorful welcome panel next to the form. */
export default function AuthLayout({ title, subtitle, children }) {
  return (
    <div className="auth-page">
      <div className="auth-shell card">
        <aside className="auth-side">
          <div className="hero-blob blob-1" />
          <div className="auth-side-content">
            <span className="brand-logo brand-logo-lg">L</span>
            <h2>{title}</h2>
            <p>{subtitle}</p>
            <ul>
              <li>📚 Courses from expert instructors</li>
              <li>📝 Interactive quizzes with instant feedback</li>
              <li>📈 Track your progress on your dashboard</li>
            </ul>
          </div>
        </aside>
        <div className="auth-form">{children}</div>
      </div>
    </div>
  );
}
