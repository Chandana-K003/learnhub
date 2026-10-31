package com.learnhub.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

public final class CourseDtos {

    private CourseDtos() {
    }

    public record CourseSummaryDto(
            Long id,
            String title,
            String description,
            String category,
            String level,
            int durationHours,
            String instructorName,
            int lessonCount,
            long studentCount) {
    }

    public record LessonDto(Long id, String title, String content, int orderIndex, int durationMinutes) {
    }

    public record QuizSummaryDto(Long id, String title, int questionCount, Integer bestScore) {
    }

    public record CourseDetailDto(
            Long id,
            String title,
            String description,
            String category,
            String level,
            int durationHours,
            String instructorName,
            long studentCount,
            boolean enrolled,
            boolean owner,
            int progressPercent,
            Set<Long> completedLessonIds,
            List<LessonDto> lessons,
            List<QuizSummaryDto> quizzes) {
    }

    public record CreateCourseRequest(
            @NotBlank(message = "Title is required") @Size(max = 150) String title,
            @NotBlank(message = "Description is required") @Size(max = 2000) String description,
            @NotBlank(message = "Category is required") @Size(max = 60) String category,
            @NotBlank(message = "Level is required") String level,
            @Min(0) int durationHours,
            @NotEmpty(message = "Add at least one lesson") List<@Valid LessonRequest> lessons,
            @Valid QuizRequest quiz) {
    }

    public record LessonRequest(
            @NotBlank(message = "Lesson title is required") @Size(max = 150) String title,
            @NotBlank(message = "Lesson content is required") @Size(max = 5000) String content,
            @Min(0) int durationMinutes) {
    }

    public record QuizRequest(
            @NotBlank(message = "Quiz title is required") String title,
            @NotEmpty(message = "Quiz needs at least one question") List<@Valid QuestionRequest> questions) {
    }

    public record QuestionRequest(
            @NotBlank(message = "Question text is required") @Size(max = 500) String text,
            @NotNull @Size(min = 2, max = 4, message = "Each question needs 2 to 4 options")
            List<@NotBlank(message = "Options cannot be empty") String> options,
            @Min(0) @Max(3) int correctOption) {
    }
}
