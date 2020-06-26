package com.team2.laps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/auth")
    public String auth() {
        return "auth/auth";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard/home";
    }
}