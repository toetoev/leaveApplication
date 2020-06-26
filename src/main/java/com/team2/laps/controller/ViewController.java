package com.team2.laps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
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