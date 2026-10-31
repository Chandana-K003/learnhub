import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client.js';

const emptyLesson = () => ({ title: '', content: '', durationMinutes: 15 });
const emptyQuestion = () => ({ text: '', options: ['', '', '', ''], correctOption: 0 });

export default function CreateCoursePage() {
  const navigate = useNavigate();
  const [course, setCourse] = useState({
    title: '', description: '', category: '', level: 'Beginner', durationHours: 5,
  });
  const [lessons, setLessons] = useState([emptyLesson()]);
  const [quizTitle, setQuizTitle] = useState('');
  const [questions, setQuestions] = useState([]);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const updateCourse = (field) => (e) => setCourse({ ...course, [field]: e.target.value });

  const updateLesson = (i, field, value) =>
    setLessons(lessons.map((l, idx) => (idx === i ? { ...l, [field]: value } : l)));

  const updateQuestion = (i, field, value) =>
    setQuestions(questions.map((q, idx) => (idx === i ? { ...q, [field]: value } : q)));

  const updateOption = (qi, oi, value) =>
    setQuestions(questions.map((q, idx) =>
      idx === qi ? { ...q, options: q.options.map((o, j) => (j === oi ? value : o)) } : q));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Drop blank options (C and D are optional) and keep the correct answer index aligned.
    const quizQuestions = questions.map((q) => {
      const filled = q.options.map((text, idx) => ({ text: text.trim(), idx })).filter((o) => o.text);
      return {
        text: q.text,
        options: filled.map((o) => o.text),
        correctOption: Math.max(0, filled.findIndex((o) => o.idx === Number(q.correctOption))),
      };
    });
    if (quizQuestions.some((q, i) => !questions[i].options[questions[i].correctOption]?.trim())) {
      setError('Each question\'s correct answer must be a filled-in option.');
      return;
    }

    const payload = {
      ...course,
      durationHours: Number(course.durationHours),
      lessons: lessons.map((l) => ({ ...l, durationMinutes: Number(l.durationMinutes) })),
      quiz: questions.length ? { title: quizTitle || `${course.title} Quiz`, questions: quizQuestions } : null,
    };

    setSaving(true);
    try {
      const created = await api.createCourse(payload);
      navigate(`/courses/${created.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <form className="create-course" onSubmit={handleSubmit}>
      <h1>Create a new course</h1>

      <section className="card form-section">
        <h2>Course details</h2>
        <label>
          Title
          <input className="input" value={course.title} onChange={updateCourse('title')} required />
        </label>
        <label>
          Description
          <textarea className="input" rows={3} value={course.description} onChange={updateCourse('description')} required />
        </label>
        <div className="form-row">
          <label>
            Category
            <input className="input" list="categories" value={course.category} onChange={updateCourse('category')} required />
            <datalist id="categories">
              <option value="Web Development" />
              <option value="Backend Development" />
              <option value="Databases" />
              <option value="Programming" />
            </datalist>
          </label>
          <label>
            Level
            <select className="input" value={course.level} onChange={updateCourse('level')}>
              <option>Beginner</option>
              <option>Intermediate</option>
              <option>Advanced</option>
            </select>
          </label>
          <label>
            Duration (hours)
            <input className="input" type="number" min={0} value={course.durationHours} onChange={updateCourse('durationHours')} />
          </label>
        </div>
      </section>

      <section className="card form-section">
        <div className="section-head">
          <h2>Lessons</h2>
          <button type="button" className="btn btn-outline btn-sm" onClick={() => setLessons([...lessons, emptyLesson()])}>
            + Add lesson
          </button>
        </div>
        {lessons.map((lesson, i) => (
          <div key={i} className="sub-form">
            <div className="section-head">
              <strong>Lesson {i + 1}</strong>
              {lessons.length > 1 && (
                <button type="button" className="link-btn" onClick={() => setLessons(lessons.filter((_, idx) => idx !== i))}>
                  Remove
                </button>
              )}
            </div>
            <div className="form-row">
              <input className="input grow" placeholder="Lesson title" value={lesson.title}
                     onChange={(e) => updateLesson(i, 'title', e.target.value)} required />
              <input className="input small" type="number" min={0} title="Minutes" value={lesson.durationMinutes}
                     onChange={(e) => updateLesson(i, 'durationMinutes', e.target.value)} />
            </div>
            <textarea className="input" rows={3} placeholder="Lesson content" value={lesson.content}
                      onChange={(e) => updateLesson(i, 'content', e.target.value)} required />
          </div>
        ))}
      </section>

      <section className="card form-section">
        <div className="section-head">
          <h2>Quiz (optional)</h2>
          <button type="button" className="btn btn-outline btn-sm" onClick={() => setQuestions([...questions, emptyQuestion()])}>
            + Add question
          </button>
        </div>
        {questions.length > 0 && (
          <input className="input" placeholder="Quiz title" value={quizTitle} onChange={(e) => setQuizTitle(e.target.value)} />
        )}
        {questions.map((q, qi) => (
          <div key={qi} className="sub-form">
            <div className="section-head">
              <strong>Question {qi + 1}</strong>
              <button type="button" className="link-btn" onClick={() => setQuestions(questions.filter((_, idx) => idx !== qi))}>
                Remove
              </button>
            </div>
            <input className="input" placeholder="Question text" value={q.text}
                   onChange={(e) => updateQuestion(qi, 'text', e.target.value)} required />
            {q.options.map((opt, oi) => (
              <label key={oi} className="option-input">
                <input type="radio" name={`correct-${qi}`} checked={Number(q.correctOption) === oi}
                       onChange={() => updateQuestion(qi, 'correctOption', oi)} />
                <input className="input" placeholder={`Option ${String.fromCharCode(65 + oi)}${oi > 1 ? ' (optional)' : ''}`}
                       value={opt} onChange={(e) => updateOption(qi, oi, e.target.value)} required={oi < 2} />
              </label>
            ))}
            <p className="muted small-text">Select the radio button next to the correct answer.</p>
          </div>
        ))}
      </section>

      {error && <p className="error">{error}</p>}
      <button className="btn btn-primary" disabled={saving}>{saving ? 'Publishing…' : 'Publish course'}</button>
    </form>
  );
}
