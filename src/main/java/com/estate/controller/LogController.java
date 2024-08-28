package com.estate.controller;

import com.estate.domain.entity.Log;
import com.estate.domain.form.LogSearch;
import com.estate.domain.service.face.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final LogService logService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Log> logs = logService.findAll(p);
        model.addAttribute("logs", logs.toList());
        model.addAttribute("totalPages", logs.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/log/list";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView viewLog(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Log> log = logService.findById(id);
        log.ifPresent(value -> {
            view.getModel().put("log", value);
            view.setViewName("admin/log/view");
        });
        return view;
    }

    @PostMapping(value="search")
    public String search(LogSearch form, Model model){
        model.addAttribute("logs", logService.search(form));
        model.addAttribute("totalPages", 1);
        model.addAttribute("currentPage", 0);
        return "admin/log/list";
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam ArrayList<Long> ids, RedirectAttributes attributes){
        return logService.deleteAllByIds(ids, attributes);
    }
}
