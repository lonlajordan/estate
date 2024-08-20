package com.estate.controller;

import com.estate.domain.entity.Housing;
import com.estate.domain.service.face.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/city")
public class CityController {
    private final CityService cityService;

    @GetMapping(value="list")
    public ModelAndView getAll(@RequestParam(required = false, defaultValue = "-1") long provinceId){
        return cityService.findAllByProvinceId(provinceId);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAllByIdIn(@RequestParam List<Long> ids, RedirectAttributes attributes){
        return cityService.deleteAll(ids, attributes);
    }

    @GetMapping(value="view/{id}")
    public ModelAndView viewCity(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Housing> city = cityService.findById(id);
        city.ifPresent(value -> {
            view.getModel().put("city", value);
            view.setViewName("admin/city/view");
        });
        return view;
    }

    @GetMapping(value = "save")
    private ModelAndView getCity(@RequestParam(required = false, defaultValue = "-1") long id){
        return cityService.getById(id);
    }

    @PostMapping(value = "save")
    public ModelAndView saveCity(@Valid Housing city, @RequestParam long provinceId, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        return cityService.createOrUpdate(city, provinceId, multiple, attributes);
    }
}
