package com.estate.controller;

import com.estate.domain.entity.Advertisement;
import com.estate.domain.service.face.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/advertisement")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @GetMapping(value="list")
    public String getAll(Model model){
        model.addAttribute("advertisements", advertisementService.findAll());
        return "admin/advertisement/list";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView view(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Advertisement> advertisement = advertisementService.findById(id);
        advertisement.ifPresent(value -> {
            view.getModel().put("advertisement", value);
            view.setViewName("admin/advertisement/view");
        });
        return view;
    }

    @GetMapping(value = "save")
    private String getAdvertisement(@RequestParam(required = false, defaultValue = "-1") long id, Model model){
        Optional<Advertisement> advertisement = advertisementService.findById(id);
        model.addAttribute("advertisement", advertisement.orElse(new Advertisement()));
        model.addAttribute("creation", advertisement.isEmpty());
        return "admin/advertisement/save";
    }

    @PostMapping(value="save")
    public ModelAndView save(@Valid Advertisement advertisement, @RequestParam(required = false) MultipartFile file, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        return advertisementService.save(advertisement, file, multiple, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        return advertisementService.deleteAllByIds(ids, attributes);
    }
}
