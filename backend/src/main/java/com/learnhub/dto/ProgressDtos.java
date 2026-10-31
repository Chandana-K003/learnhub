package com.learnhub.dto;

import com.learnhub.dto.AuthDtos.UserDto;
import com.learnhub.dto.CourseDtos.CourseSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

public final class ProgressDtos {

    private ProgressDtos() {
    }

    public record EnrollmentDto(
            Long courseId,
            String courseTitle,
            String category,
            String level,
            String instructorName,
            LocalDateTime enrolledAt,
            int completedLessons,
            int totalLessons,
            int progressPercent,
            boolean completed) {
    }

    public record AttemptDto(
            Long id,
            Long quizId,
            String quizTitle,
            String courseTitle,
            int score,
            int total,
            int percent,
            LocalDateTime attemptedAt) {
    }

    public record DashboardDto(
            UserDto user,
            int enrolledCount,
            int completedCount,
            int averageQuizPercent,
            int quizzesTaken,
            List<EnrollmentDto> enrollments,
            List<AttemptDto> recentAttempts,
            List<CourseSummaryDto> teachingCourses) {
    }
}
