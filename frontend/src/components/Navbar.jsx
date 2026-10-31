import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { initials } from '../theme.js';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="navbar">
      <div className="container navbar-inner">
        <Link to="/" className="brand">
          <span className="brand-logo">L</span>
          Learn<span className="brand-accent">Hub</span>
        </Link>
        <nav className="nav-links">
          <NavLink to="/" end>Courses</NavLink>
          {user && <NavLink to="/dashboard">Dashboard</NavLink>}
          {user?.role === 'INSTRUCTOR' && <NavLink to="/create-course">Create Course</NavLink>}
        </nav>
        <div className="nav-user">
          {user ? (
            <>
              <div className="nav-profile">
                <span className="avatar">{initials(user.fullName)}</span>
                <span className="nav-name">
                  {user.fullName}
                  <small>{user.role.toLowerCase()}</small>
                </span>
              </div>
              <button className="btn btn-ghost btn-sm" onClick={handleLogout}>Log out</button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-ghost btn-sm">Log in</Link>
              <Link to="/register" className="btn btn-primary btn-sm">Sign up free</Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
