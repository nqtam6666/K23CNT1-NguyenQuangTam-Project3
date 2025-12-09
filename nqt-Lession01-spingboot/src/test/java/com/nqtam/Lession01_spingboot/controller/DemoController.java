package com.nqtam.Lession01_spingboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class DemoController {

    @GetMapping("/demo")
    public Map<String, Object> demoApi() {
        MultiInheritance obj = new MultiInheritance();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", obj.runAll());
        return response;
    }


}
