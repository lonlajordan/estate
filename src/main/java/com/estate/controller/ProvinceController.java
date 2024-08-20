package com.estate.controller;

import com.estate.domain.entity.Province;
import com.estate.domain.service.face.ProvinceService;
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
@RequestMapping("/province")
public class ProvinceController {
    private final ProvinceService provinceService;

    @GetMapping(value="list")
    public String getAll(Model model){
        model.addAttribute("provinces", provinceService.findAll());
        return "admin/province/list";
    }
    
    @GetMapping(value="save")
    public String getProvince(@RequestParam(required = false, defaultValue = "-1") long id, Model model){
        Optional<Province> province = provinceService.findById(id);
        model.addAttribute("province", province.orElse(new Province()));
        return "admin/province/save";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView viewProvince(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Province> province = provinceService.findById(id);
        province.ifPresent(value -> {
            view.getModel().put("province", value);
            view.setViewName("admin/province/view");
        });
        return view;
    }

    @PostMapping(value="save")
    public ModelAndView saveProvince(@Valid Province province, BindingResult result, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        if(result.hasErrors()) return new ModelAndView("admin/province/save");
        return provinceService.createOrUpdate(province, multiple, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteProvinces(@RequestParam ArrayList<Long> ids, RedirectAttributes attributes){
        return provinceService.deleteAllByIds(ids, attributes);
    }
}
