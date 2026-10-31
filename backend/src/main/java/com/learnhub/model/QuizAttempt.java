package com.learnhub.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private int score;

    private int totalQuestions;

    @Column(nullable = false)
    private LocalDateTime attemptedAt = LocalDateTime.now();

    public QuizAttempt() {
    }

    public QuizAttempt(User user, Quiz quiz, int score, int totalQuestions) {
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    public int getPercent() {
        return totalQuestions == 0 ? 0 : Math.round(score * 100f / totalQuestions);
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Quiz getQuiz() { return quiz; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
}
