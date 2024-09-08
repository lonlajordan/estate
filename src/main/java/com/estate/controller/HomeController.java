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
        return "public/index3";
    }

    @GetMapping("/login")
    public String login(){
        return "public/login";
    }

    @GetMapping("faq")
    public String faq(){
        return "public/faq";
    }

    @GetMapping("blog")
    public String blog(){
        return "public/blog";
    }

    @GetMapping("cart")
    public String cart(){
        return "public/cart";
    }

    @GetMapping("forget-password")
    public String forgetPassword(){
        return "public/forget-password";
    }

    @GetMapping("pricing")
    public String pricing(){
        return "public/pricing";
    }

    @GetMapping("team")
    public String team(){
        return "public/team";
    }

    @GetMapping("account")
    public String account(){
        return "public/account";
    }
}
