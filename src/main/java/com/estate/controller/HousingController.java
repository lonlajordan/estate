package com.estate.controller;

import com.estate.domain.entity.Housing;
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

    @GetMapping("list")
    public String findAll(Model model){
        model.addAttribute("standings", standingService.findAll());
        model.addAttribute("housings", housingService.findAll());
        return "admin/housing/list";
    }

    @GetMapping("save")
    public String findById(@RequestParam(required = false) Long id, Model model){
        Housing housing = id == null ? new Housing() : housingService.findById(id).orElse(new Housing());
        model.addAttribute("housing", housing);
        model.addAttribute("standings", standingService.findAll());
        return "admin/housing/save";
    }

    @PostMapping("save")
    public String save(){
        return "redirect:/housing/list";
    }
}
