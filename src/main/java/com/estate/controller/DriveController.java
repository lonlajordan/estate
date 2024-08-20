package com.estate.controller;

import com.estate.domain.entity.Game;
import com.estate.domain.enumaration.GameType;
import com.estate.domain.dto.Notification;
import com.estate.domain.service.face.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/drive")
public class DriveController {
    private final GameService gameService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Game> games = gameService.findAllByTypeIn(Collections.singletonList(GameType.DRIVE_TEST), p);
        model.addAttribute("drives", games.toList());
        model.addAttribute("totalPages", games.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/drive/list";
    }

    @GetMapping(value = "save")
    private ModelAndView getDrive(@RequestParam(required = false) Long id, @RequestParam(required = false) GameType type, @RequestParam(required = false, defaultValue = "false") boolean generate){
        return gameService.getById(id, type, generate);
    }

    @GetMapping(value="participation/{id}")
    public String practice(@PathVariable long id, Model model){
        gameService.findParticipation(id, model);
        return "admin/drive/practice";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView getDetails(@PathVariable long id){
        return gameService.driveDetails(id);
    }

    @PostMapping(value="report/{id}")
    public String report(@PathVariable long id, RedirectAttributes attributes) throws FileNotFoundException {
        gameService.driveReport(id);
        attributes.addFlashAttribute("notification", Notification.info());
        return "redirect:/drive/view/" + id;
    }

    @PostMapping(value="save")
    public RedirectView save(@RequestParam long id, @RequestParam List<String> questionIds, @RequestParam String advertisementIds, @RequestParam int duration, RedirectAttributes attributes){
        return gameService.updateBattleOrDrive(id, String.join(",", questionIds), advertisementIds, duration, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        gameService.deleteAllByIds(ids, attributes);
        return new RedirectView("/drive/list", true);
    }
}
