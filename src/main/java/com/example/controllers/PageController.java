package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/users-view")
    public String usersView() {
        return "users-view";  // This will resolve to src/main/resources/templates/users-view.html
    }
}

