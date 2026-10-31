package com.learnhub.service;

import com.learnhub.dto.ProgressDtos.AttemptDto;
import com.learnhub.dto.QuizDtos.*;
import com.learnhub.exception.ApiException;
import com.learnhub.model.Question;
import com.learnhub.model.Quiz;
import com.learnhub.model.QuizAttempt;
import com.learnhub.model.User;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.QuizAttemptRepository;
import com.learnhub.repository.QuizRepository;
import com.learnhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       EnrollmentRepository enrollmentRepository,
                       UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public QuizDto getQuiz(Long quizId, User user) {
        Quiz quiz = findAccessibleQuiz(quizId, user);
        List<QuestionDto> questions = quiz.getQuestions().stream()
                .map(q -> new QuestionDto(q.getId(), q.getQuestionText(), q.getOptions()))
                .toList();
        return new QuizDto(quiz.getId(), quiz.getTitle(), quiz.getCourse().getId(),
                quiz.getCourse().getTitle(), questions);
    }

    @Transactional
    public QuizResultDto submit(Long quizId, QuizSubmission submission, User user) {
        Quiz quiz = findAccessibleQuiz(quizId, user);

        int score = 0;
        List<QuestionResult> results = new ArrayList<>();
        for (Question q : quiz.getQuestions()) {
            Integer selected = submission.answers().get(q.getId());
            boolean correct = selected != null && selected == q.getCorrectOption();
            if (correct) {
                score++;
            }
            results.add(new QuestionResult(q.getId(), selected, q.getCorrectOption(), correct));
        }

        QuizAttempt attempt = quizAttemptRepository.save(new QuizAttempt(
                userRepository.getReferenceById(user.getId()), quiz, score, quiz.getQuestions().size()));
        return new QuizResultDto(attempt.getId(), score, attempt.getTotalQuestions(), attempt.getPercent(), results);
    }

    @Transactional(readOnly = true)
    public List<AttemptDto> attemptsOf(User user) {
        return quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(user.getId()).stream()
                .map(a -> new AttemptDto(a.getId(), a.getQuiz().getId(), a.getQuiz().getTitle(),
                        a.getQuiz().getCourse().getTitle(), a.getScore(), a.getTotalQuestions(),
                        a.getPercent(), a.getAttemptedAt()))
                .toList();
    }

    /** Only enrolled students and the course instructor may open a quiz. */
    private Quiz findAccessibleQuiz(Long quizId, User user) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> ApiException.notFound("Quiz not found"));
        Long courseId = quiz.getCourse().getId();
        boolean owner = quiz.getCourse().getInstructor().getId().equals(user.getId());
        if (!owner && !enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw ApiException.forbidden("Enroll in the course to take this quiz");
        }
        return quiz;
    }
}
