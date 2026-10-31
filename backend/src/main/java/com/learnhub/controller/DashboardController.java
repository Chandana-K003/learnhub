package com.learnhub.controller;

import com.learnhub.dto.ProgressDtos.DashboardDto;
import com.learnhub.model.User;
import com.learnhub.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardDto dashboard(@AuthenticationPrincipal User user) {
        return dashboardService.build(user);
    }
}
