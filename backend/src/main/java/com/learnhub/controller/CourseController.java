package com.learnhub.controller;

import com.learnhub.dto.CourseDtos.CourseDetailDto;
import com.learnhub.dto.CourseDtos.CourseSummaryDto;
import com.learnhub.dto.CourseDtos.CreateCourseRequest;
import com.learnhub.model.User;
import com.learnhub.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseSummaryDto> list(@RequestParam(name = "search", required = false) String search,
                                       @RequestParam(name = "category", required = false) String category) {
        return courseService.search(search, category);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return courseService.categories();
    }

    /** Public endpoint; user is null for visitors who are not logged in. */
    @GetMapping("/{id}")
    public CourseDetailDto get(@PathVariable("id") Long id, @AuthenticationPrincipal User user) {
        return courseService.getDetail(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public CourseDetailDto create(@Valid @RequestBody CreateCourseRequest request,
                                  @AuthenticationPrincipal User user) {
        return courseService.create(request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal User user) {
        courseService.delete(id, user);
    }
}
