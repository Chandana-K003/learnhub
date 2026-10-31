package com.learnhub.repository;

import com.learnhub.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    long countByEnrollmentId(Long enrollmentId);

    @Modifying
    @Query("delete from LessonProgress p where p.enrollment.id = :enrollmentId")
    void deleteByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    @Modifying
    @Query("delete from LessonProgress p where p.enrollment.id in "
            + "(select e.id from Enrollment e where e.course.id = :courseId)")
    void deleteByCourseId(@Param("courseId") Long courseId);
}
