package com.learnhub.service;

import com.learnhub.dto.AuthDtos.UserDto;
import com.learnhub.dto.CourseDtos.CourseSummaryDto;
import com.learnhub.dto.ProgressDtos.AttemptDto;
import com.learnhub.dto.ProgressDtos.DashboardDto;
import com.learnhub.dto.ProgressDtos.EnrollmentDto;
import com.learnhub.model.Role;
import com.learnhub.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final EnrollmentService enrollmentService;
    private final QuizService quizService;
    private final CourseService courseService;

    public DashboardService(EnrollmentService enrollmentService, QuizService quizService,
                            CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.quizService = quizService;
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public DashboardDto build(User user) {
        List<EnrollmentDto> enrollments = enrollmentService.myEnrollments(user);
        List<AttemptDto> attempts = quizService.attemptsOf(user);
        List<CourseSummaryDto> teaching = user.getRole() == Role.INSTRUCTOR
                ? courseService.coursesTaughtBy(user.getId())
                : List.of();

        int completed = (int) enrollments.stream().filter(EnrollmentDto::completed).count();
        int averageQuiz = (int) Math.round(attempts.stream().mapToInt(AttemptDto::percent).average().orElse(0));

        return new DashboardDto(
                UserDto.from(user),
                enrollments.size(),
                completed,
                averageQuiz,
                attempts.size(),
                enrollments,
                attempts.stream().limit(5).toList(),
                teaching);
    }
}
