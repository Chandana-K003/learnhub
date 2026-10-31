package com.learnhub.service;

import com.learnhub.dto.CourseDtos.*;
import com.learnhub.exception.ApiException;
import com.learnhub.model.*;
import com.learnhub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public CourseService(CourseRepository courseRepository,
                         UserRepository userRepository,
                         EnrollmentRepository enrollmentRepository,
                         LessonProgressRepository lessonProgressRepository,
                         QuizAttemptRepository quizAttemptRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDto> search(String q, String category) {
        return courseRepository.search(blankToNull(q), blankToNull(category)).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> categories() {
        return courseRepository.findAllCategories();
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDto> coursesTaughtBy(Long instructorId) {
        return courseRepository.findByInstructorIdOrderByCreatedAtDesc(instructorId).stream()
                .map(this::toSummary)
                .toList();
    }

    /** Lesson content is only returned to enrolled students and the course owner. */
    @Transactional(readOnly = true)
    public CourseDetailDto getDetail(Long courseId, User user) {
        Course course = findCourse(courseId);
        Enrollment enrollment = user == null ? null
                : enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId).orElse(null);
        boolean owner = user != null && course.getInstructor().getId().equals(user.getId());
        boolean canViewContent = enrollment != null || owner;

        Set<Long> completedLessonIds = enrollment == null ? Set.of()
                : lessonProgressRepository.findByEnrollmentId(enrollment.getId()).stream()
                .map(p -> p.getLesson().getId())
                .collect(Collectors.toSet());

        List<LessonDto> lessons = course.getLessons().stream()
                .map(l -> new LessonDto(l.getId(), l.getTitle(), canViewContent ? l.getContent() : null,
                        l.getOrderIndex(), l.getDurationMinutes()))
                .toList();

        List<QuizSummaryDto> quizzes = course.getQuizzes().stream()
                .map(qz -> new QuizSummaryDto(qz.getId(), qz.getTitle(), qz.getQuestions().size(),
                        user == null ? null : quizAttemptRepository.findBestScore(user.getId(), qz.getId())))
                .toList();

        return new CourseDetailDto(
                course.getId(), course.getTitle(), course.getDescription(), course.getCategory(),
                course.getLevel(), course.getDurationHours(), course.getInstructor().getFullName(),
                enrollmentRepository.countByCourseId(courseId),
                enrollment != null, owner,
                percent(completedLessonIds.size(), lessons.size()),
                completedLessonIds, lessons, quizzes);
    }

    @Transactional
    public CourseDetailDto create(CreateCourseRequest request, User instructor) {
        Course course = new Course();
        course.setTitle(request.title().trim());
        course.setDescription(request.description().trim());
        course.setCategory(request.category().trim());
        course.setLevel(request.level().trim());
        course.setDurationHours(request.durationHours());
        course.setInstructor(userRepository.getReferenceById(instructor.getId()));

        int order = 1;
        for (LessonRequest lr : request.lessons()) {
            course.addLesson(new Lesson(lr.title().trim(), lr.content().trim(), order++, lr.durationMinutes()));
        }

        if (request.quiz() != null) {
            Quiz quiz = new Quiz(request.quiz().title().trim());
            for (QuestionRequest qr : request.quiz().questions()) {
                if (qr.correctOption() >= qr.options().size()) {
                    throw ApiException.badRequest("Correct answer must be one of the options for: " + qr.text());
                }
                quiz.addQuestion(new Question(qr.text().trim(), qr.options(), qr.correctOption()));
            }
            course.addQuiz(quiz);
        }

        courseRepository.save(course);
        return getDetail(course.getId(), instructor);
    }

    @Transactional
    public void delete(Long courseId, User user) {
        Course course = findCourse(courseId);
        if (!course.getInstructor().getId().equals(user.getId())) {
            throw ApiException.forbidden("Only the course instructor can delete this course");
        }
        // Remove rows that reference this course before deleting it.
        lessonProgressRepository.deleteByCourseId(courseId);
        enrollmentRepository.deleteByCourseId(courseId);
        quizAttemptRepository.deleteByCourseId(courseId);
        courseRepository.delete(course);
    }

    Course findCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> ApiException.notFound("Course not found"));
    }

    CourseSummaryDto toSummary(Course c) {
        return new CourseSummaryDto(
                c.getId(), c.getTitle(), c.getDescription(), c.getCategory(), c.getLevel(),
                c.getDurationHours(), c.getInstructor().getFullName(), c.getLessons().size(),
                enrollmentRepository.countByCourseId(c.getId()));
    }

    static int percent(int part, int total) {
        return total == 0 ? 0 : Math.round(part * 100f / total);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
