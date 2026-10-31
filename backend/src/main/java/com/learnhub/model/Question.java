package com.learnhub.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(nullable = false, length = 500)
    private String questionText;

    @Column(nullable = false)
    private String optionA;

    @Column(nullable = false)
    private String optionB;

    private String optionC;

    private String optionD;

    /** Index (0-3) of the correct option. */
    private int correctOption;

    public Question() {
    }

    public Question(String questionText, List<String> options, int correctOption) {
        this.questionText = questionText;
        this.optionA = options.size() > 0 ? options.get(0) : null;
        this.optionB = options.size() > 1 ? options.get(1) : null;
        this.optionC = options.size() > 2 ? options.get(2) : null;
        this.optionD = options.size() > 3 ? options.get(3) : null;
        this.correctOption = correctOption;
    }

    public List<String> getOptions() {
        List<String> options = new ArrayList<>();
        for (String option : new String[]{optionA, optionB, optionC, optionD}) {
            if (option != null && !option.isBlank()) {
                options.add(option);
            }
        }
        return options;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public int getCorrectOption() { return correctOption; }
    public void setCorrectOption(int correctOption) { this.correctOption = correctOption; }
}
