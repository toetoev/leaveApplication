package com.team2.laps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ViewConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/view/auth").setViewName("/auth/auth");
        registry.addViewController("/view/dashboard").setViewName("/dashboard/home");
        registry.addViewController("/view/staff/leave").setViewName("/dashboard/staff/leave");
        registry.addViewController("/view/staff/compensation").setViewName("/dashboard/staff/compensation");
        registry.addViewController("/view/staff/submit").setViewName("/dashboard/staff/submit");
        registry.addViewController("/view/admin/signup").setViewName("/dashboard/admin/signup");
    }
}