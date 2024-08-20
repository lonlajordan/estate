package com.estate.controller;

import com.estate.domain.entity.Setting;
import com.estate.domain.service.face.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/setting")
public class SettingController {
    private final SettingService settingService;

    @GetMapping(value="list")
    public String findAll(Model model){
        model.addAttribute("settings", settingService.findAll());
        return "admin/setting/list";
    }

    @GetMapping(value = "save")
    private String getSetting(@RequestParam long id, Model model){
        model.addAttribute("setting", settingService.findById(id).orElse(null));
        return "admin/setting/save";
    }

    @PostMapping(value="save")
    public RedirectView save(@Valid Setting setting, RedirectAttributes attributes){
        return settingService.update(setting, attributes);
    }
}
