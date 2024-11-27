package com.estate.controller;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Setting;
import com.estate.domain.enumaration.Profil;
import com.estate.domain.enumaration.SettingCode;
import com.estate.domain.form.ContactForm;
import com.estate.domain.form.VisitorForm;
import com.estate.domain.mail.SmsHelper;
import com.estate.domain.service.face.*;
import com.estate.domain.service.impl.PictureServiceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    private final HousingService housingService;
    private final SettingService settingService;
    private final StandingService standingService;
    private final VisitorService visitorService;
    private final PartnerService partnerService;
    private final TestimonialService testimonialService;
    private final PictureServiceServiceImpl pictureService;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("countryCodes", SmsHelper.countryCodes);
        model.addAttribute("contact",new ContactForm());
        model.addAttribute("visitor",new VisitorForm());
        model.addAttribute("users", userService.findAllByProfil(Profil.STAFF));
        model.addAttribute("housings", housingService.findAll());
        model.addAttribute("standings", standingService.findAllByActiveTrueOrderByRentAsc());
        model.addAttribute("telephone", settingService.findByCode(SettingCode.TELEPHONE_PUBLIC).map(Setting::getValue).orElse(""));
        model.addAttribute("whatsapp", settingService.findByCode(SettingCode.WHATSAPP).map(Setting::getValue).orElse(""));
        model.addAttribute("email", settingService.findByCode(SettingCode.EMAIL_PUBLIC).map(Setting::getValue).orElse(""));
        model.addAttribute("localisation", settingService.findByCode(SettingCode.ADDRESS_PUBLIC).map(Setting::getValue).orElse(""));
        model.addAttribute("partners",partnerService.findAll());
        model.addAttribute("testimonials",testimonialService.findAll());
        model.addAttribute("pictures",pictureService.findAll());
        return "index";
    }

    @PostMapping("contact")
    public String contact(@Valid @ModelAttribute("contact") ContactForm contact, RedirectAttributes attributes){
        Notification notification = visitorService.contact(contact);
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/";
    }

    @PostMapping("subscribe")
    public String subscribe(@Valid @ModelAttribute("visitor") VisitorForm visitor, RedirectAttributes attributes){
        Notification notification = visitorService.subscribe(visitor);
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/";
    }

    @GetMapping("contract")
    public String contract(){
        return "contract";
    }
}
