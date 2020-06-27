package com.team2.laps.config;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder = new Jackson2ObjectMapperBuilder();
        jackson2ObjectMapperBuilder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy/MM/dd-HH:mm"));
        return jackson2ObjectMapperBuilder;
    }
}