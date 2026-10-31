import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import AuthLayout from '../components/AuthLayout.jsx';

export default function LoginPage() {
  const { login, sessionExpired } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(email, password);
      navigate(location.state?.from || '/dashboard', { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const fillDemo = (demoEmail) => {
    setEmail(demoEmail);
    setPassword('password123');
  };

  return (
    <AuthLayout title="Welcome back!" subtitle="Log in to continue your courses and quizzes.">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Log in</h1>
        <p className="muted form-sub">Enter your details to access your account.</p>
        {sessionExpired && <p className="notice">Your session has expired. Please log in again.</p>}
        {!sessionExpired && location.state?.from && (
          <p className="notice">Please log in to open that page.</p>
        )}
        <label>
          Email
          <input className="input" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Password
          <input className="input" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </label>
        {error && <p className="error">{error}</p>}
        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? 'Logging in…' : 'Log in'}
        </button>
        <p className="muted">
          No account? <Link to="/register">Sign up</Link>
        </p>
        <div className="demo">
          <span className="muted">Demo accounts:</span>
          <button type="button" className="btn btn-outline btn-sm" onClick={() => fillDemo('student@learnhub.com')}>
            Student
          </button>
          <button type="button" className="btn btn-outline btn-sm" onClick={() => fillDemo('instructor@learnhub.com')}>
            Instructor
          </button>
        </div>
      </form>
    </AuthLayout>
  );
}
