import { Link } from 'react-router-dom';
import { categoryTheme, initials } from '../theme.js';

export default function CourseCard({ course }) {
  const theme = categoryTheme(course.category);

  return (
    <Link to={`/courses/${course.id}`} className="card course-card">
      <div className="course-banner" style={{ background: theme.gradient }}>
        <span className="course-icon">{theme.icon}</span>
        <span className="level-pill">{course.level}</span>
      </div>
      <div className="course-body">
        <span className="tag">{course.category}</span>
        <h3>{course.title}</h3>
        <p className="muted clamp">{course.description}</p>
        <div className="course-instructor">
          <span className="avatar avatar-sm">{initials(course.instructorName)}</span>
          {course.instructorName}
        </div>
        <div className="course-footer">
          <span>📚 {course.lessonCount} lessons</span>
          <span>⏱ {course.durationHours}h</span>
          <span>👥 {course.studentCount}</span>
        </div>
      </div>
    </Link>
  );
}
