import { useState } from 'react';

/**
 * Lists a course's lessons. Lesson content is only present when the user may view it
 * (enrolled or course owner); otherwise lessons are shown as locked.
 */
export default function LessonList({ lessons, completedIds, canMarkComplete, onComplete }) {
  const [openId, setOpenId] = useState(null);
  const [busyId, setBusyId] = useState(null);

  const handleComplete = async (lessonId) => {
    setBusyId(lessonId);
    try {
      await onComplete(lessonId);
    } finally {
      setBusyId(null);
    }
  };

  return (
    <ol className="lesson-list">
      {lessons.map((lesson) => {
        const done = completedIds.includes(lesson.id);
        const locked = lesson.content == null;
        const open = openId === lesson.id;

        return (
          <li key={lesson.id} className={`lesson ${done ? 'lesson-done' : ''}`}>
            <button
              className="lesson-header"
              onClick={() => !locked && setOpenId(open ? null : lesson.id)}
              disabled={locked}
            >
              <span className="lesson-status">{done ? '✅' : locked ? '🔒' : '▶️'}</span>
              <span className="lesson-title">
                {lesson.orderIndex}. {lesson.title}
              </span>
              <span className="muted">{lesson.durationMinutes} min</span>
            </button>
            {open && (
              <div className="lesson-content">
                <p>{lesson.content}</p>
                {canMarkComplete && !done && (
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={() => handleComplete(lesson.id)}
                    disabled={busyId === lesson.id}
                  >
                    {busyId === lesson.id ? 'Saving…' : 'Mark as complete'}
                  </button>
                )}
              </div>
            )}
          </li>
        );
      })}
    </ol>
  );
}
