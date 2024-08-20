package com.estate.controller;

import com.estate.domain.service.face.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("home")
    private String home(Model model){
        model.addAttribute("users", userService.countUsers());
        return "index";
    }
}