package com.learnhub.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public final class QuizDtos {

    private QuizDtos() {
    }

    /** A question as sent to the learner: the correct answer is never included. */
    public record QuestionDto(Long id, String text, List<String> options) {
    }

    public record QuizDto(Long id, String title, Long courseId, String courseTitle, List<QuestionDto> questions) {
    }

    /** Maps question id to the selected option index. */
    public record QuizSubmission(@NotNull Map<Long, Integer> answers) {
    }

    public record QuestionResult(Long questionId, Integer selectedOption, int correctOption, boolean correct) {
    }

    public record QuizResultDto(Long attemptId, int score, int total, int percent, List<QuestionResult> results) {
    }
}
