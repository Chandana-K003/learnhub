import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import AuthLayout from '../components/AuthLayout.jsx';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: '', email: '', password: '', role: 'STUDENT' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await register(form);
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Join LearnHub" subtitle="Create a free account and start learning today.">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Create your account</h1>
        <p className="muted form-sub">It only takes a minute.</p>
        <label>
          Full name
          <input className="input" value={form.fullName} onChange={update('fullName')} required />
        </label>
        <label>
          Email
          <input className="input" type="email" value={form.email} onChange={update('email')} required />
        </label>
        <label>
          Password
          <input className="input" type="password" minLength={6} value={form.password} onChange={update('password')} required />
        </label>
        <label>
          I want to
          <select className="input" value={form.role} onChange={update('role')}>
            <option value="STUDENT">Learn (Student)</option>
            <option value="INSTRUCTOR">Teach (Instructor)</option>
          </select>
        </label>
        {error && <p className="error">{error}</p>}
        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? 'Creating account…' : 'Sign up'}
        </button>
        <p className="muted">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </form>
    </AuthLayout>
  );
}
