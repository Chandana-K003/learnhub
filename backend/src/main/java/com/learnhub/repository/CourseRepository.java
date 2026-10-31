package com.learnhub.repository;

import com.learnhub.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("""
            select c from Course c
            where (:q is null
                   or lower(c.title) like lower(concat('%', :q, '%'))
                   or lower(c.description) like lower(concat('%', :q, '%')))
              and (:category is null or c.category = :category)
            order by c.createdAt desc
            """)
    List<Course> search(@Param("q") String q, @Param("category") String category);

    @Query("select distinct c.category from Course c order by c.category")
    List<String> findAllCategories();

    List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);
}
