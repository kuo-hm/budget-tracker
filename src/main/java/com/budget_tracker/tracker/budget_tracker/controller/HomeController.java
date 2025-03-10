package com.budget_tracker.tracker.budget_tracker.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class HomeController {
    
    @GetMapping("get")
    public String getMethodName() {
        return "Hello!!";
    }
    
}
