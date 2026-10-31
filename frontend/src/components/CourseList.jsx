import CourseCard from './CourseCard.jsx';

export default function CourseList({ courses, emptyMessage = 'No courses found.' }) {
  if (!courses.length) {
    return <p className="empty">{emptyMessage}</p>;
  }
  return (
    <div className="course-grid">
      {courses.map((course) => (
        <CourseCard key={course.id} course={course} />
      ))}
    </div>
  );
}
