package com.estate.controller;

import com.estate.domain.entity.Merchant;
import com.estate.domain.service.face.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/merchant")
public class MerchantController {
    private final MerchantService merchantService;

    @GetMapping(value="list")
    public String getAll(Model model){
        model.addAttribute("merchants", merchantService.findAll());
        return "admin/merchant/list";
    }

    @GetMapping(value="save")
    public String getMerchant(@RequestParam(required = false, defaultValue = "-1") int id, Model model){
        Optional<Merchant> merchant = merchantService.findById(id);
        model.addAttribute("merchant", merchant.orElse(new Merchant()));
        return "admin/merchant/save";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView viewMerchant(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Merchant> merchant = merchantService.findById(id);
        merchant.ifPresent(value -> {
            view.getModel().put("merchant", value);
            view.setViewName("admin/merchant/view");
        });
        return view;
    }

    @GetMapping(value="toggle/{id}")
    public RedirectView toggleMerchant(@PathVariable long id, RedirectAttributes attributes){
        attributes.addFlashAttribute("notification", merchantService.toggleById(id));
        return new RedirectView("/merchant/list", true);
    }

    @PostMapping(value="save")
    public ModelAndView saveMerchant(@Valid Merchant merchant, BindingResult result, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        if(result.hasErrors()) return new ModelAndView("admin/merchant/save");
        return merchantService.createOrUpdate(merchant, multiple, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteMerchants(@RequestParam ArrayList<Long> ids, RedirectAttributes attributes){
        return merchantService.deleteAllByIds(ids, attributes);
    }
}
