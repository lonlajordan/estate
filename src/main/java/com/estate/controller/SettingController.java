package com.estate.controller;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Setting;
import com.estate.domain.form.PolicyForm;
import com.estate.domain.form.SettingForm;
import com.estate.domain.service.face.SettingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

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
    public String save(@Valid @ModelAttribute("setting") SettingForm setting, BindingResult result, RedirectAttributes attributes){
        if(result.hasErrors()) return "admin/setting/save";
        attributes.addFlashAttribute("notification", settingService.update(setting));
        return "redirect:/setting/list";
    }

    @PostMapping(value="policy")
    public String policy(@Valid @ModelAttribute("policy") PolicyForm policy, BindingResult result, RedirectAttributes attributes){
        Notification notification;
        if(result.hasErrors()){
            notification = Notification.error(StringUtils.capitalize(Optional.ofNullable(result.getFieldError()).map(DefaultMessageSourceResolvable::getDefaultMessage).orElse("Fichier invalide")));
        } else {
            notification = settingService.savePolicy(policy.getFile());
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/setting/list";
    }
}
