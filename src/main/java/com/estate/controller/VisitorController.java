package com.estate.controller;


import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Visitor;
import com.estate.domain.form.VisitorSearchForm;
import com.estate.domain.service.face.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/visitor")
public class VisitorController {
    private final VisitorService visitorService;

    @GetMapping("list")
    public String list(Model model, @RequestParam(required = false, defaultValue = "1") int page, HttpServletRequest request){
        Page<Visitor> visitors;
        VisitorSearchForm visitorSearchForm = new VisitorSearchForm();
        Map<String, ?> attributes = RequestContextUtils.getInputFlashMap(request);
        boolean search = attributes != null && attributes.containsKey("searchForm");
        if(search){
            visitorSearchForm = (VisitorSearchForm) attributes.get("searchForm");
            visitors = visitorService.findAll(visitorSearchForm);
            if(visitors.isEmpty()) model.addAttribute("notification", Notification.info("Aucun résultat"));
        } else {
            visitors = visitorService.findAll(page);
        }

        model.addAttribute("visitors", visitors.toList());
        model.addAttribute("totalPages", visitors.getTotalPages());
        model.addAttribute("currentPage", visitors.getNumber() + 1);
        model.addAttribute("searchForm", visitorSearchForm);
        model.addAttribute("search", search);
        return "admin/visitor/list";

    }

    @PostMapping("search")
    public String search(VisitorSearchForm form, RedirectAttributes attributes){
        attributes.addFlashAttribute("searchForm", form);
        return "redirect:/visitor/list";
    }


}
