package com.learnhub.repository;

import com.learnhub.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<Enrollment> findByUserIdOrderByEnrolledAtDesc(Long userId);

    long countByCourseId(Long courseId);

    @Modifying
    @Query("delete from Enrollment e where e.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
}
