package com.learnhub.controller;

import com.learnhub.dto.QuizDtos.QuizDto;
import com.learnhub.dto.QuizDtos.QuizResultDto;
import com.learnhub.dto.QuizDtos.QuizSubmission;
import com.learnhub.model.User;
import com.learnhub.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/{id}")
    public QuizDto get(@PathVariable("id") Long id, @AuthenticationPrincipal User user) {
        return quizService.getQuiz(id, user);
    }

    @PostMapping("/{id}/submit")
    public QuizResultDto submit(@PathVariable("id") Long id,
                                @Valid @RequestBody QuizSubmission submission,
                                @AuthenticationPrincipal User user) {
        return quizService.submit(id, submission, user);
    }
}
