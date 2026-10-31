package com.learnhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * When deployed, the built React app is served by Spring Boot from /static.
 * React Router handles these URLs in the browser, so on a page refresh or a direct
 * link the server must return index.html instead of a 404.
 */
@Controller
public class SpaController {

    @GetMapping({"/login", "/register", "/dashboard", "/create-course", "/courses/{id}", "/quizzes/{id}"})
    public String forwardToReactApp() {
        return "forward:/index.html";
    }
}
