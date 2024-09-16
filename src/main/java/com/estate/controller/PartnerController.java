package com.estate.controller;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Partner;
import com.estate.domain.form.PartnerForm;
import com.estate.domain.service.face.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/partner")
public class PartnerController {
    private final PartnerService partnerService;

    @GetMapping(value="list")
    public String findAll(Model model){
        model.addAttribute("partners", partnerService.findAll());
        return "admin/partner/list";
    }

    @GetMapping(value="view/{id}")
    public String findById(@PathVariable long id, Model model, RedirectAttributes attributes){
        Partner partner = partnerService.findById(id).orElse(null);
        if(partner == null){
            attributes.addFlashAttribute("notification", Notification.error("Partenaire introuvable."));
            return "redirect:/partner/list";
        }
        model.addAttribute("partner", partner);
        return "admin/partner/view";
    }

    @GetMapping(value = "save")
    private String findById(@RequestParam(required = false) Long id, Model model, RedirectAttributes attributes){
        Partner partner = new Partner();
        if(id != null)  partner = partnerService.findById(id).orElse(null);
        if(partner == null){
            attributes.addFlashAttribute("notification", Notification.error("Partenaire introuvable"));
            return "redirect:/partner/list";
        }
        model.addAttribute("partner", partner.toForm());
        return "admin/partner/save";
    }

    @PostMapping(value="save")
    public String save(@Valid @ModelAttribute("partner") PartnerForm partner, BindingResult result, @RequestParam(required = false, defaultValue = "false") boolean multiple, Model model, RedirectAttributes attributes){
        if(result.hasErrors()) return "admin/partner/save";
        Notification notification =  partnerService.save(partner);
        if(multiple || notification.hasError()){
            model.addAttribute("notification", notification);
            model.addAttribute("partner", notification.hasError() ? partner : new PartnerForm());
            return "admin/partner/save";
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/partner/list";
    }

    @RequestMapping(value="delete")
    public String deleteById(@RequestParam long id, RedirectAttributes attributes, HttpServletRequest request){
        Notification notification =  partnerService.deleteById(id, request);
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/partner/list";
    }
}
