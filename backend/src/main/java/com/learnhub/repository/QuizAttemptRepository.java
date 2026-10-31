package com.learnhub.repository;

import com.learnhub.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserIdOrderByAttemptedAtDesc(Long userId);

    @Query("select max(a.score) from QuizAttempt a where a.user.id = :userId and a.quiz.id = :quizId")
    Integer findBestScore(@Param("userId") Long userId, @Param("quizId") Long quizId);

    @Modifying
    @Query("delete from QuizAttempt a where a.quiz.id in "
            + "(select q.id from Quiz q where q.course.id = :courseId)")
    void deleteByCourseId(@Param("courseId") Long courseId);
}
