package com.estate.controller;

import com.estate.domain.entity.Standing;
import com.estate.domain.service.face.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/standing")
public class StandingController {
    private final StandingService standingService;

    @GetMapping(value="list")
    public String getAll(Model model){
        model.addAttribute("standings", standingService.findAll());
        return "admin/standing/list";
    }


    @GetMapping(value="view/{id}")
    public ModelAndView getDetails(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Standing> standing = standingService.findById(id);
        standing.ifPresent(value -> {
            view.getModel().put("standing", value);
            view.setViewName("admin/standing/view");
        });
        return view;
    }

    @GetMapping(value = "save")
    private String getStanding(@RequestParam(required = false, defaultValue = "-1") long id, Model model){
        Optional<Standing> standing = standingService.findById(id);
        model.addAttribute("standing", standing.orElse(new Standing()));
        model.addAttribute("creation", standing.isEmpty());
        return "admin/standing/save";
    }

    @PostMapping(value="save")
    public ModelAndView save(@Valid Standing standing, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        return standingService.save(standing, multiple, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        return standingService.deleteAllByIdIn(ids, attributes);
    }
}
