package com.estate.controller;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Standing;
import com.estate.domain.form.StandingForm;
import com.estate.domain.service.face.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/standing")
public class StandingController {
    private final StandingService standingService;

    @GetMapping(value="list")
    public String findAll(Model model){
        model.addAttribute("standings", standingService.findAll());
        return "admin/standing/list";
    }

    @GetMapping(value = "save")
    private String findById(@RequestParam(required = false) Long id, Model model, RedirectAttributes attributes){
        Standing standing = new Standing();
        if(id != null)  standing = standingService.findById(id).orElse(null);
        if(standing == null){
            attributes.addFlashAttribute("notification", Notification.error("Standing introuvable"));
            return "redirect:/standing/list";
        }
        model.addAttribute("standing", standing.toForm());
        return "admin/standing/save";
    }


    @GetMapping(value="view/{id}")
    public String findById(@PathVariable long id, Model model, RedirectAttributes attributes){
        Standing standing = standingService.findById(id).orElse(null);
        if(standing == null){
            attributes.addFlashAttribute("notification", Notification.error("Standing introuvable"));
            return "redirect:/standing/list";
        }
        model.addAttribute("standing", standing);
        return "admin/standing/view";
    }

    @RequestMapping(value="toggle/{id}")
    public String toggle(@PathVariable long id, RedirectAttributes attributes, Principal principal){
        attributes.addFlashAttribute("notification", standingService.toggleById(id, principal));
        return "redirect:/standing/list";
    }

    @PostMapping(value="save")
    public String save(@Valid @ModelAttribute("standing") StandingForm standing, BindingResult result, @RequestParam(required = false, defaultValue = "false") boolean multiple, Model model, RedirectAttributes attributes, Principal principal){
        if(result.hasErrors()) return "admin/standing/save";
        Notification notification =  standingService.save(standing, principal);
        if(multiple || notification.hasError()){
            model.addAttribute("notification", notification);
            model.addAttribute("standing", notification.hasError() ? standing : new StandingForm());
            return "admin/standing/save";
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/standing/list";
    }

    @RequestMapping(value="delete")
    public String deleteById(@RequestParam long id, @RequestParam(required = false, defaultValue = "false") boolean force, RedirectAttributes attributes, HttpServletRequest request){
        Notification notification =  standingService.deleteById(id, force, request);
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/standing/list";
    }
}
