package com.Ngay02.nqt_Ngay02.pkg_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class AppConfig {
    @Bean
    public String appName() {
        return "<h1> Nguyễn Quang Tâm </h1><h2>Spring Boot Application";
    }
}