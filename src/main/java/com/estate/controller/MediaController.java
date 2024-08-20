package com.estate.controller;

import com.estate.domain.entity.Media;
import com.estate.domain.service.face.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/media")
public class MediaController {
    private final MediaService mediaService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Media> medias = mediaService.mediaList(p);
        model.addAttribute("medias", medias.toList());
        model.addAttribute("totalPages", medias.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/media/list";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView view(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Media> media = mediaService.findById(id);
        media.ifPresent(value -> {
            view.getModel().put("media", value);
            view.setViewName("admin/media/view");
        });
        return view;
    }

    @GetMapping(value = "save")
    private String getMedia(@RequestParam(required = false, defaultValue = "-1") long id, Model model){
        Optional<Media> media = mediaService.findById(id);
        model.addAttribute("media", media.orElse(new Media()));
        model.addAttribute("creation", media.isEmpty());
        return "admin/media/save";
    }

    @PostMapping(value="save")
    public ModelAndView save(@Valid Media media, @RequestParam(required = false) MultipartFile file, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        return mediaService.save(media, file, multiple, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        return mediaService.deleteAllByIds(ids, attributes);
    }
}
