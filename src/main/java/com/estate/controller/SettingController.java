package com.estate.controller;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Setting;
import com.estate.domain.form.SettingForm;
import com.estate.domain.service.face.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

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
    private String findById(@RequestParam long id, Model model, RedirectAttributes attributes){
        Setting setting = settingService.findById(id).orElse(null);
        if(setting == null){
            attributes.addFlashAttribute("notification", Notification.error("Paramètre introuvable"));
            return "redirect:/setting/list";
        }
        model.addAttribute("setting", setting.toForm());
        return "admin/setting/save";
    }

    @PostMapping(value="save")
    public String save(@Valid @ModelAttribute("setting") SettingForm setting, BindingResult result, Model model, RedirectAttributes attributes, Principal principal){
        if(result.hasErrors()) return "admin/setting/save";
        attributes.addFlashAttribute("notification", settingService.update(setting, principal));
        return "redirect:/setting/list";
    }

    @PostMapping(value="policy")
    public String policy(@RequestParam MultipartFile file, RedirectAttributes attributes){
        attributes.addFlashAttribute("notification", settingService.savePolicy(file));
        return "redirect:/setting/list";
    }
}
