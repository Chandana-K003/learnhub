import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client.js';
import { useAuth } from '../context/AuthContext.jsx';
import CourseList from '../components/CourseList.jsx';
import { categoryTheme } from '../theme.js';

export default function CoursesPage() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.getCategories().then(setCategories).catch(() => {});
  }, []);

  // Debounce the search so we don't call the API on every keystroke.
  useEffect(() => {
    const timer = setTimeout(() => {
      setLoading(true);
      api.getCourses(search, category)
        .then((data) => {
          setCourses(data);
          setError('');
        })
        .catch((err) => setError(err.message))
        .finally(() => setLoading(false));
    }, 300);
    return () => clearTimeout(timer);
  }, [search, category]);

  return (
    <>
      <section className="hero">
        <div className="hero-blob blob-1" />
        <div className="hero-blob blob-2" />
        <div className="hero-content">
          <span className="hero-eyebrow">✨ Learn anything, anytime</span>
          <h1>
            Build skills that <span className="gradient-text">shape your future</span>
          </h1>
          <p>
            Hands-on courses in web development, backend engineering, databases and programming —
            with interactive quizzes and progress tracking.
          </p>
          <div className="hero-actions">
            {user ? (
              <Link to="/dashboard" className="btn btn-light">Go to my dashboard →</Link>
            ) : (
              <>
                <Link to="/register" className="btn btn-light">Start learning free →</Link>
                <Link to="/login" className="btn btn-glass">I have an account</Link>
              </>
            )}
          </div>
        </div>
        <div className="hero-art">
          <div className="float-card fc-1">⚛️ React</div>
          <div className="float-card fc-2">🍃 Spring MVC</div>
          <div className="float-card fc-3">🗄️ MySQL</div>
          <div className="hero-emoji">🎓</div>
        </div>
      </section>

      <section className="toolbar card">
        <div className="search">
          <span className="search-icon">🔍</span>
          <input
            placeholder="Search for a course…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="chips">
          <button className={`chip ${category === '' ? 'chip-active' : ''}`} onClick={() => setCategory('')}>
            All
          </button>
          {categories.map((c) => (
            <button
              key={c}
              className={`chip ${category === c ? 'chip-active' : ''}`}
              onClick={() => setCategory(c)}
            >
              {categoryTheme(c).icon} {c}
            </button>
          ))}
        </div>
      </section>

      <div className="section-head">
        <h2>{category || 'All courses'}</h2>
        {!loading && <span className="muted">{courses.length} course{courses.length === 1 ? '' : 's'}</span>}
      </div>

      {error && <p className="error">{error} — is the backend running on port 8080?</p>}
      {loading ? <p className="muted">Loading courses…</p> : <CourseList courses={courses} />}
    </>
  );
}
