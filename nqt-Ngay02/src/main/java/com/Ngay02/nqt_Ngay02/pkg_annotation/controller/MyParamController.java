package com.Ngay02.nqt_Ngay02.pkg_annotation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class MyParamController
{
    // Sử dụng @RequestParam để lấy query parameter từ URL
    @GetMapping("/my-param")
    public String searchUsers(@RequestParam(value = "name",
            required = false) String name) {
        if (name == null) {
            return "<h2>No name provided, returning all users";
        }
        return "<h1>Searching for users with name: " + name;
    }

    // Sử dụng @PathVariable để lấy giá trị từ đường dẫn URL
    @GetMapping("/my-variable/{id}")
    public String getUserById(@PathVariable(required = false)
                              String id) {
        return "<h1>User ID is " + id;
    }
}