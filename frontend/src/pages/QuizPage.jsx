import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api/client.js';
import QuizPlayer from '../components/QuizPlayer.jsx';

export default function QuizPage() {
  const { id } = useParams();
  const [quiz, setQuiz] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.getQuiz(id).then(setQuiz).catch((err) => setError(err.message));
  }, [id]);

  if (error) return <p className="error">{error}</p>;
  if (!quiz) return <p className="muted">Loading quiz…</p>;

  return (
    <div className="quiz-page">
      <Link to={`/courses/${quiz.courseId}`} className="back">← {quiz.courseTitle}</Link>
      <h1>{quiz.title}</h1>
      <QuizPlayer quiz={quiz} onSubmit={(answers) => api.submitQuiz(id, answers)} />
    </div>
  );
}
