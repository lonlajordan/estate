package com.estate.controller;

import com.estate.domain.entity.Mystery;
import com.estate.domain.service.face.MysteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/mystery")
public class MysteryController {
    private final MysteryService mysteryService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Mystery> mysteries = mysteryService.mysteryList(p);
        model.addAttribute("mysteries", mysteries.toList());
        model.addAttribute("totalPages", mysteries.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/mystery/list";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView view(@PathVariable long id){
        return mysteryService.details(id);
    }

    @GetMapping(value="import")
    public String importation(){
        return "admin/mystery/import";
    }

    @GetMapping(value = "save")
    private String getMystery(@RequestParam(required = false, defaultValue = "-1") long id, Model model){
        Optional<Mystery> mystery = mysteryService.findById(id);
        model.addAttribute("mystery", mystery.orElse(new Mystery()));
        model.addAttribute("creation", mystery.isEmpty());
        return "admin/mystery/save";
    }

    @PostMapping(value="save")
    public ModelAndView save(@Valid Mystery mystery, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        return mysteryService.createOrUpdate(mystery, multiple, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        return mysteryService.deleteAllByIds(ids, attributes);
    }
}
