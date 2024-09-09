package com.estate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @PostMapping("contact")
    public String contact(){
        return "index";
    }

    @GetMapping("/")
    public String home(){
        return "index";
    }
}
