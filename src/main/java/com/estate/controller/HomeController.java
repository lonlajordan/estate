package com.estate.controller;

import com.estate.domain.entity.Setting;
import com.estate.domain.enumaration.Profil;
import com.estate.domain.enumaration.SettingCode;
import com.estate.domain.form.ContactForm;
import com.estate.domain.form.VisitorForm;
import com.estate.domain.mail.SmsHelper;
import com.estate.domain.service.face.HousingService;
import com.estate.domain.service.face.SettingService;
import com.estate.domain.service.face.StandingService;
import com.estate.domain.service.face.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    private final HousingService housingService;
    private final SettingService settingService;
    private final StandingService standingService;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("countryCodes", SmsHelper.countryCodes);
        model.addAttribute("contact",new ContactForm());
        model.addAttribute("visitor",new VisitorForm());
        model.addAttribute("users", userService.findAllByProfil(Profil.STAFF));
        model.addAttribute("housings", housingService.findAll());
        model.addAttribute("standings", standingService.findAllByActiveTrueOrderByRentAsc());
        model.addAttribute("telephone", settingService.findByCode(SettingCode.TELEPHONE_PUBLIC).map(Setting::getValue).orElse(""));
        model.addAttribute("email", settingService.findByCode(SettingCode.EMAIL_PUBLIC).map(Setting::getValue).orElse(""));
        model.addAttribute("localisation", settingService.findByCode(SettingCode.ADDRESS_PUBLIC).map(Setting::getValue).orElse(""));
        return "index";
    }

    @PostMapping("contact")
    public String contact(@Valid @ModelAttribute("contact") ContactForm contact){
        return "index";
    }

    @PostMapping("/")
    public String notifyForContact(@ModelAttribute ContactForm contact,Model model){
        System.out.println("Dans le post");
        model.addAttribute("contact",contact);
        //System.out.println(contact.getName() + " ++++ " + contact.getEmail() + " +++++ " + contact.getPhone() );
        return "index";
    }

    @PostMapping("subscribe")
    public String subscribe(@Valid @ModelAttribute("visitor") VisitorForm visitor){
        return "index";
    }
}
