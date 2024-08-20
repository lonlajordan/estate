package com.estate.controller;

import com.estate.domain.enumaration.Operator;
import com.estate.domain.enumaration.Status;
import com.estate.domain.entity.Recharge;
import com.estate.domain.service.face.RechargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recharge")
public class RechargeController {
    private final RechargeService rechargeService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Recharge> recharges = rechargeService.findAll(p);
        model.addAttribute("recharges", recharges.toList());
        model.addAttribute("totalPages", recharges.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/recharge/list";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView getDetails(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Recharge> recharge = rechargeService.findById(id);
        recharge.ifPresent(value -> {
            view.getModel().put("recharge", value);
            view.setViewName("admin/recharge/view");
        });
        return view;
    }

    @GetMapping(value="status/{id}")
    public String getAll(@PathVariable long id, RedirectAttributes attributes){
        attributes.addFlashAttribute("notification", rechargeService.status(id));
        return "redirect:/recharge/list";
    }

    @PostMapping(value="search")
    public ModelAndView search(@RequestParam(required = false, defaultValue = "1") int page,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) Operator operator,
                         @RequestParam(required = false) Status status,
                         @RequestParam(required = false, defaultValue = "1970-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
                         @RequestParam(required = false, defaultValue = "1970-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end){
        return rechargeService.search(name, phone, operator, status, start, end, page);
    }

    @GetMapping(value="statistic")
    public ModelAndView statistics(){
        return rechargeService.statistics();
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        return rechargeService.deleteAllByIds(ids, attributes);
    }
}
