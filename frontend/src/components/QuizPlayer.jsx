import { useState } from 'react';
import ProgressBar from './ProgressBar.jsx';

/**
 * Interactive quiz: one question at a time, then a results review.
 * onSubmit(answers) must return the server's result { score, total, percent, results }.
 */
export default function QuizPlayer({ quiz, onSubmit }) {
  const [index, setIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [result, setResult] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const questions = quiz.questions;
  const question = questions[index];
  const answeredCount = Object.keys(answers).length;
  const isLast = index === questions.length - 1;

  const choose = (optionIndex) => setAnswers({ ...answers, [question.id]: optionIndex });

  const submit = async () => {
    setSubmitting(true);
    setError('');
    try {
      setResult(await onSubmit(answers));
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const restart = () => {
    setAnswers({});
    setIndex(0);
    setResult(null);
  };

  if (result) {
    const resultById = Object.fromEntries(result.results.map((r) => [r.questionId, r]));
    return (
      <div className="quiz">
        <div className={`card quiz-score ${result.percent >= 50 ? 'pass' : 'fail'}`}>
          <h2>{result.percent >= 50 ? '🎉 Well done!' : '📖 Keep practising!'}</h2>
          <p className="score-big">{result.score} / {result.total}</p>
          <p>You scored {result.percent}%</p>
          <button className="btn btn-primary" onClick={restart}>Try again</button>
        </div>

        <h3>Review answers</h3>
        {questions.map((q, i) => {
          const r = resultById[q.id];
          return (
            <div key={q.id} className="card review">
              <p><strong>{i + 1}. {q.text}</strong></p>
              <ul className="options">
                {q.options.map((opt, oi) => {
                  let cls = 'option';
                  if (oi === r.correctOption) cls += ' option-correct';
                  else if (oi === r.selectedOption) cls += ' option-wrong';
                  return <li key={oi} className={cls}>{opt}</li>;
                })}
              </ul>
              {r.selectedOption == null && <p className="muted">Not answered</p>}
            </div>
          );
        })}
      </div>
    );
  }

  return (
    <div className="quiz">
      <ProgressBar
        percent={Math.round(((index + 1) / questions.length) * 100)}
        label={`Question ${index + 1} of ${questions.length}`}
      />

      <div className="card question-card">
        <h3>{question.text}</h3>
        <ul className="options">
          {question.options.map((opt, oi) => (
            <li key={oi}>
              <button
                className={`option option-btn ${answers[question.id] === oi ? 'option-selected' : ''}`}
                onClick={() => choose(oi)}
              >
                <span className="option-letter">{String.fromCharCode(65 + oi)}</span>
                {opt}
              </button>
            </li>
          ))}
        </ul>
      </div>

      {error && <p className="error">{error}</p>}

      <div className="quiz-nav">
        <button className="btn btn-outline" disabled={index === 0} onClick={() => setIndex(index - 1)}>
          ← Previous
        </button>
        {isLast ? (
          <button className="btn btn-primary" disabled={submitting || answeredCount === 0} onClick={submit}>
            {submitting ? 'Submitting…' : `Submit (${answeredCount}/${questions.length} answered)`}
          </button>
        ) : (
          <button className="btn btn-primary" onClick={() => setIndex(index + 1)}>
            Next →
          </button>
        )}
      </div>
    </div>
  );
}
