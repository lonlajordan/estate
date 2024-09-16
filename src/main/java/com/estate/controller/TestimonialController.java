package com.estate.controller;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Testimonial;
import com.estate.domain.form.TestimonialForm;
import com.estate.domain.service.face.TestimonialService;
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
@RequestMapping("/testimonial")
public class TestimonialController {
    private final TestimonialService testimonialService;

    @GetMapping(value="list")
    public String findAll(Model model){
        model.addAttribute("testimonials", testimonialService.findAll());
        return "admin/testimonial/list";
    }

    @GetMapping(value="view/{id}")
    public String findById(@PathVariable long id, Model model, RedirectAttributes attributes){
        Testimonial testimonial = testimonialService.findById(id).orElse(null);
        if(testimonial == null){
            attributes.addFlashAttribute("notification", Notification.error("Témoignage introuvable."));
            return "redirect:/testimonial/list";
        }
        model.addAttribute("testimonial", testimonial);
        return "admin/testimonial/view";
    }

    @GetMapping(value = "save")
    private String findById(@RequestParam(required = false) Long id, Model model, RedirectAttributes attributes){
        Testimonial testimonial = new Testimonial();
        if(id != null)  testimonial = testimonialService.findById(id).orElse(null);
        if(testimonial == null){
            attributes.addFlashAttribute("notification", Notification.error("Témoignage introuvable"));
            return "redirect:/testimonial/list";
        }
        model.addAttribute("testimonial", testimonial.toForm());
        return "admin/testimonial/save";
    }

    @PostMapping(value="save")
    public String save(@Valid @ModelAttribute("testimonial") TestimonialForm testimonial, BindingResult result, @RequestParam(required = false, defaultValue = "false") boolean multiple, Model model, RedirectAttributes attributes){
        if(result.hasErrors()) return "admin/testimonial/save";
        Notification notification =  testimonialService.save(testimonial);
        if(multiple || notification.hasError()){
            model.addAttribute("notification", notification);
            model.addAttribute("testimonial", notification.hasError() ? testimonial : new TestimonialForm());
            return "admin/testimonial/save";
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/testimonial/list";
    }

    @RequestMapping(value="delete")
    public String deleteById(@RequestParam long id, RedirectAttributes attributes, HttpServletRequest request){
        Notification notification =  testimonialService.deleteById(id, request);
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/testimonial/list";
    }
}
