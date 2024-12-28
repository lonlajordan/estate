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
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final LogService logService;

    @GetMapping(value="list")
    public String findAll(@RequestParam(required = false, defaultValue = "1") int page, Model model, HttpServletRequest request){
        LogSearch form = new LogSearch();
        Page<Log> logs;
        Map<String, ?> attributes = RequestContextUtils.getInputFlashMap(request);
        boolean search = attributes != null && attributes.containsKey("searchForm");
        if(search){
            form = (LogSearch) attributes.get("searchForm");
            logs = logService.findAll(form);
            if(logs.isEmpty()) model.addAttribute("notification", Notification.info("Aucun résultat"));
        } else {
            logs = logService.findAll(page);
        }
        model.addAttribute("logs", logs.toList());
        model.addAttribute("totalPages", logs.getTotalPages());
        model.addAttribute("currentPage", logs.getPageable().getPageNumber());
        model.addAttribute("searchForm", form);
        model.addAttribute("search", search);
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

    @RequestMapping(value="remove")
    public RedirectView deleteById(@RequestParam Long id, RedirectAttributes attributes){
        return logService.deleteAllByIds(Collections.singletonList(id), attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam ArrayList<Long> ids, RedirectAttributes attributes){
        return logService.deleteAllByIds(ids, attributes);
    }
}
