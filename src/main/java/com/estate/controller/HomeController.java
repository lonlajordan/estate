package com.estate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("contact")
    public String contact(){
        return "public/contact";
    }

    @GetMapping("about")
    public String about(){
        return "public/about";
    }

    @GetMapping("/")
    public String index(){
        return "public/index2";
    }

    @GetMapping("/login")
    public String login(){
        return "public/login";
    }

    @GetMapping("faq")
    public String faq(){
        return "public/faq";
    }
}
