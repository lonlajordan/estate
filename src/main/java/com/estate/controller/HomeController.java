package com.estate.controller;

import com.estate.domain.form.ContactForm;
import com.estate.domain.mail.SmsHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class HomeController {

    @PostMapping("contact")
    public String contact(){
        return "index";
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("countryCodes", SmsHelper.countryCodes);
        model.addAttribute("contact",new ContactForm());
        return "index";
    }

    @PostMapping("contact/info")
    public String notifyForContact(@Valid @ModelAttribute("student") ContactForm contact){

        return "index";
    }
}
