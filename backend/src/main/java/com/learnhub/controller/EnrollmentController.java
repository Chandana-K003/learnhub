package com.learnhub.controller;

import com.learnhub.dto.ProgressDtos.EnrollmentDto;
import com.learnhub.model.User;
import com.learnhub.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/my")
    public List<EnrollmentDto> myEnrollments(@AuthenticationPrincipal User user) {
        return enrollmentService.myEnrollments(user);
    }

    @PostMapping("/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentDto enroll(@PathVariable("courseId") Long courseId, @AuthenticationPrincipal User user) {
        return enrollmentService.enroll(courseId, user);
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@PathVariable("courseId") Long courseId, @AuthenticationPrincipal User user) {
        enrollmentService.unenroll(courseId, user);
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public EnrollmentDto completeLesson(@PathVariable("lessonId") Long lessonId,
                                        @AuthenticationPrincipal User user) {
        return enrollmentService.completeLesson(lessonId, user);
    }
}
