package com.Ngay02.nqt_Ngay02.ioc_spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class GreetingController {
    private final GreetingService greetingService;
    // Sử dụng Constructor-based Dependency Injection
    @Autowired
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
    @GetMapping("/greet")
    public String greet() {
        return greetingService.greet("Quang Tâm nè");
    }
}
@RestController
class HelloController {
    @GetMapping("/hello")
    public String sayHello() {
        System.out.println("Hellooooo, Spring Boot!");
        return "Hello, Spring Boot!";
    }
}
