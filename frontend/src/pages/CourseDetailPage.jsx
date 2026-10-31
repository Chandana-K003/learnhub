import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { api } from '../api/client.js';
import { useAuth } from '../context/AuthContext.jsx';
import LessonList from '../components/LessonList.jsx';
import ProgressBar from '../components/ProgressBar.jsx';
import { categoryTheme } from '../theme.js';

export default function CourseDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [course, setCourse] = useState(null);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const load = useCallback(() => {
    api.getCourse(id)
      .then(setCourse)
      .catch((err) => setError(err.message));
  }, [id]);

  // Reload when the logged-in user changes, since enrollment status depends on it.
  useEffect(load, [load, user]);

  const runAction = async (action) => {
    setBusy(true);
    setError('');
    try {
      await action();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  const enroll = () => {
    if (!user) {
      navigate('/login', { state: { from: `/courses/${id}` } });
      return;
    }
    runAction(async () => {
      await api.enroll(id);
      load();
    });
  };

  const unenroll = () => {
    if (!window.confirm('Leave this course? Your progress will be lost.')) return;
    runAction(async () => {
      await api.unenroll(id);
      load();
    });
  };

  const deleteCourse = () => {
    if (!window.confirm('Delete this course permanently?')) return;
    runAction(async () => {
      await api.deleteCourse(id);
      navigate('/dashboard');
    });
  };

  const completeLesson = async (lessonId) => {
    try {
      await api.completeLesson(lessonId);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  if (error && !course) return <p className="error">{error}</p>;
  if (!course) return <p className="muted">Loading…</p>;

  return (
    <div className="detail">
      <Link to="/" className="back">← All courses</Link>

      <section className="detail-header" style={{ background: categoryTheme(course.category).gradient }}>
        <span className="detail-icon">{categoryTheme(course.category).icon}</span>
        <div className="tags">
          <span className="tag tag-light">{course.category}</span>
          <span className="tag tag-light">{course.level}</span>
        </div>
        <h1>{course.title}</h1>
        <p>{course.description}</p>
        <div className="course-meta">
          <span>👤 {course.instructorName}</span>
          <span>📚 {course.lessons.length} lessons</span>
          <span>⏱ {course.durationHours} hours</span>
          <span>🎓 {course.studentCount} enrolled</span>
        </div>

        {course.enrolled && (
          <div className="detail-progress">
            <ProgressBar percent={course.progressPercent} />
          </div>
        )}

        <div className="actions">
          {course.owner ? (
            <>
              <span className="badge">You teach this course</span>
              <button className="btn btn-danger" onClick={deleteCourse} disabled={busy}>Delete course</button>
            </>
          ) : course.enrolled ? (
            <button className="btn btn-outline" onClick={unenroll} disabled={busy}>Leave course</button>
          ) : (
            <button className="btn btn-primary" onClick={enroll} disabled={busy}>
              {user ? 'Enroll now — free' : 'Log in to enroll'}
            </button>
          )}
        </div>
        {error && <p className="error">{error}</p>}
      </section>

      <div className="detail-grid">
        <section>
          <h2>Lessons</h2>
          {!course.enrolled && !course.owner && (
            <p className="muted">Enroll to unlock the lesson content.</p>
          )}
          <LessonList
            lessons={course.lessons}
            completedIds={course.completedLessonIds}
            canMarkComplete={course.enrolled}
            onComplete={completeLesson}
          />
        </section>

        <aside>
          <h2>Quizzes</h2>
          {course.quizzes.length === 0 && <p className="muted">No quizzes for this course.</p>}
          {course.quizzes.map((quiz) => (
            <div key={quiz.id} className="card quiz-item">
              <strong>📝 {quiz.title}</strong>
              <p className="muted">{quiz.questionCount} questions</p>
              {quiz.bestScore != null && (
                <p>Best score: {quiz.bestScore}/{quiz.questionCount}</p>
              )}
              {course.enrolled || course.owner ? (
                <Link to={`/quizzes/${quiz.id}`} className="btn btn-primary btn-sm">
                  {quiz.bestScore != null ? 'Retake quiz' : 'Start quiz'}
                </Link>
              ) : (
                <span className="muted">🔒 Enroll to take this quiz</span>
              )}
            </div>
          ))}
        </aside>
      </div>
    </div>
  );
}
