package com.learnhub.service;

import com.learnhub.dto.ProgressDtos.EnrollmentDto;
import com.learnhub.exception.ApiException;
import com.learnhub.model.*;
import com.learnhub.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             LessonProgressRepository lessonProgressRepository,
                             LessonRepository lessonRepository,
                             UserRepository userRepository,
                             CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.courseService = courseService;
    }

    @Transactional
    public EnrollmentDto enroll(Long courseId, User user) {
        Course course = courseService.findCourse(courseId);
        if (course.getInstructor().getId().equals(user.getId())) {
            throw ApiException.badRequest("You cannot enroll in your own course");
        }
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new ApiException(HttpStatus.CONFLICT, "You are already enrolled in this course");
        }
        Enrollment enrollment = enrollmentRepository.save(
                new Enrollment(userRepository.getReferenceById(user.getId()), course));
        return toDto(enrollment);
    }

    @Transactional
    public void unenroll(Long courseId, User user) {
        Enrollment enrollment = findEnrollment(user.getId(), courseId);
        lessonProgressRepository.deleteByEnrollmentId(enrollment.getId());
        enrollmentRepository.delete(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDto> myEnrollments(User user) {
        return enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(user.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public EnrollmentDto completeLesson(Long lessonId, User user) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> ApiException.notFound("Lesson not found"));
        Enrollment enrollment = findEnrollment(user.getId(), lesson.getCourse().getId());

        if (!lessonProgressRepository.existsByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)) {
            lessonProgressRepository.save(new LessonProgress(enrollment, lesson));
        }

        int done = (int) lessonProgressRepository.countByEnrollmentId(enrollment.getId());
        int total = enrollment.getCourse().getLessons().size();
        enrollment.setCompleted(total > 0 && done >= total);
        return toDto(enrollment);
    }

    private Enrollment findEnrollment(Long userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> ApiException.forbidden("You are not enrolled in this course"));
    }

    EnrollmentDto toDto(Enrollment e) {
        Course c = e.getCourse();
        int total = c.getLessons().size();
        int done = e.getId() == null ? 0 : (int) lessonProgressRepository.countByEnrollmentId(e.getId());
        return new EnrollmentDto(
                c.getId(), c.getTitle(), c.getCategory(), c.getLevel(), c.getInstructor().getFullName(),
                e.getEnrolledAt(), done, total, CourseService.percent(done, total), e.isCompleted());
    }
}
