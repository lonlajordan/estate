package com.estate.controller;

import com.estate.domain.service.face.HousingService;
import com.estate.domain.service.face.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/housing")
public class HousingController {
    private final HousingService housingService;
    private final StandingService standingService;

    @GetMapping(value="list")
    public String findAll(Model model){
        model.addAttribute("standings", standingService.findAll());
        model.addAttribute("housings", housingService.findAll());
        return "admin/housing/list";
    }
}
