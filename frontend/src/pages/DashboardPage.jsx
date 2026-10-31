import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client.js';
import StatCard from '../components/StatCard.jsx';
import ProgressBar from '../components/ProgressBar.jsx';
import CourseList from '../components/CourseList.jsx';
import { categoryTheme } from '../theme.js';

const TITLES = ['dr', 'dr.', 'mr', 'mr.', 'mrs', 'mrs.', 'ms', 'ms.', 'prof', 'prof.'];

/** First name without titles, e.g. "Dr. Anita Sharma" -> "Anita". */
function firstName(fullName) {
  const parts = fullName.split(' ').filter((p) => p && !TITLES.includes(p.toLowerCase()));
  return parts[0] || fullName;
}

export default function DashboardPage() {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.getDashboard().then(setData).catch((err) => setError(err.message));
  }, []);

  if (error) return <p className="error">{error}</p>;
  if (!data) return <p className="muted">Loading dashboard…</p>;

  const isInstructor = data.user.role === 'INSTRUCTOR';

  return (
    <div className="dashboard">
      <section className="welcome">
        <div>
          <span className="hero-eyebrow">{isInstructor ? '🧑‍🏫 Instructor' : '🎒 Student'} dashboard</span>
          <h1>Welcome back, {firstName(data.user.fullName)}! 👋</h1>
          <p>
            {isInstructor
              ? `You are teaching ${data.teachingCourses.length} course${data.teachingCourses.length === 1 ? '' : 's'}. Share your knowledge with more learners!`
              : data.enrolledCount > 0
                ? 'Pick up where you left off and keep your learning streak going.'
                : 'Start your learning journey by enrolling in your first course.'}
          </p>
        </div>
        <Link to="/" className="btn btn-light">Explore courses →</Link>
      </section>

      <div className="stats">
        <StatCard icon="📚" label="Enrolled courses" value={data.enrolledCount} color="#7c3aed" />
        <StatCard icon="🏆" label="Completed courses" value={data.completedCount} color="#10b981" />
        <StatCard icon="📝" label="Quizzes taken" value={data.quizzesTaken} color="#0ea5e9" />
        <StatCard icon="🎯" label="Average quiz score" value={`${data.averageQuizPercent}%`} color="#f59e0b" />
      </div>

      {isInstructor && (
        <section>
          <div className="section-head">
            <h2>Courses you teach</h2>
            <Link to="/create-course" className="btn btn-primary btn-sm">+ New course</Link>
          </div>
          <CourseList courses={data.teachingCourses} emptyMessage="You haven't created any courses yet." />
        </section>
      )}

      <section>
        <h2>My learning</h2>
        {data.enrollments.length === 0 ? (
          <p className="empty">
            You are not enrolled in any course yet. <Link to="/">Browse courses</Link>
          </p>
        ) : (
          <div className="enrollment-list">
            {data.enrollments.map((e) => (
              <Link to={`/courses/${e.courseId}`} key={e.courseId} className="card enrollment">
                <span className="enrollment-icon" style={{ background: categoryTheme(e.category).gradient }}>
                  {categoryTheme(e.category).icon}
                </span>
                <div>
                  <h3>{e.courseTitle} {e.completed && <span className="badge badge-success">Completed</span>}</h3>
                  <p className="muted">
                    {e.category} · {e.instructorName} · {e.completedLessons}/{e.totalLessons} lessons
                  </p>
                </div>
                <ProgressBar percent={e.progressPercent} />
              </Link>
            ))}
          </div>
        )}
      </section>

      <section>
        <h2>Recent quiz attempts</h2>
        {data.recentAttempts.length === 0 ? (
          <p className="empty">No quiz attempts yet.</p>
        ) : (
          <div className="card table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Quiz</th>
                  <th>Course</th>
                  <th>Score</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {data.recentAttempts.map((a) => (
                  <tr key={a.id}>
                    <td><Link to={`/quizzes/${a.quizId}`}>{a.quizTitle}</Link></td>
                    <td>{a.courseTitle}</td>
                    <td>
                      <span className={`badge ${a.percent >= 50 ? 'badge-success' : 'badge-danger'}`}>
                        {a.score}/{a.total} ({a.percent}%)
                      </span>
                    </td>
                    <td>{new Date(a.attemptedAt).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}
