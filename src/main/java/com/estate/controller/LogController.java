package com.estate.controller;

import com.estate.domain.entity.Log;
import com.estate.domain.entity.Notification;
import com.estate.domain.form.LogSearch;
import com.estate.domain.service.face.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final LogService logService;

    @GetMapping(value="list")
    public String findAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Log> logs = logService.findAll(p);
        model.addAttribute("logs", logs.toList());
        model.addAttribute("totalPages", logs.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/log/list";
    }

    @GetMapping(value="view/{id}")
    public String findById(@PathVariable long id, Model model, RedirectAttributes attributes){
        Log log = logService.findById(id).orElse(null);
        if(log == null){
            attributes.addFlashAttribute("notification", Notification.error("Évènement introuvable"));
            return "redirect:/log/list";
        }
        model.addAttribute("log", log);
        return "admin/log/view";
    }

    @PostMapping("search")
    public String search(LogSearch form, RedirectAttributes attributes){
        attributes.addFlashAttribute("searchForm", form);
        return "redirect:/log/list";
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam ArrayList<Long> ids, RedirectAttributes attributes){
        return logService.deleteAllByIds(ids, attributes);
    }
}
