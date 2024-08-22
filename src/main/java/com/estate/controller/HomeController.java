package com.estate.controller;

import com.estate.domain.enumaration.Status;
import com.estate.domain.service.face.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class HomeController {
    private final StudentService studentService;
    private final HousingService housingService;
    private final PaymentService paymentService;
    private final UserService userService;

    @GetMapping("home")
    private String home(Model model){
        model.addAttribute("users", userService.count());
        model.addAttribute("students", studentService.count());
        model.addAttribute("housings", housingService.count());
        model.addAttribute("payments", paymentService.countByStatus(Status.PENDING));
        return "index";
    }
}