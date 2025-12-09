package com.Ngay02.nqt_Ngay02.ioc_spring;

import org.springframework.stereotype.Service;
@Service
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String greet(String name) {
        return "<h2>K23CNT1[Spring Boot!] Xin chào,</h2> " +
                "<h1 style='color:red; text-align:center'>" +
                name;
    }
}