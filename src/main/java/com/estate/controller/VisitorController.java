package com.estate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VisitorController {

    @GetMapping("contact")
    public String contact(){
        return "public/contact";
    }

    @GetMapping("about")
    public String about(){
        return "public/about";
    }

    @GetMapping("faq")
    public String faq(){
        return "public/faq";
    }
}
