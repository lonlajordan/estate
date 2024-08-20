package com.estate.controller;

import com.estate.domain.entity.Game;
import com.estate.domain.enumaration.GameType;
import com.estate.domain.service.face.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/battle")
public class BattleController {
    private final GameService gameService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Game> games = gameService.findAllByTypeIn(Collections.singletonList(GameType.BATTLE), p);
        model.addAttribute("battles", games.toList());
        model.addAttribute("totalPages", games.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/battle/list";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView getBattleById(@PathVariable long id){
        return gameService.getBattleDetails(id);
    }

    @GetMapping(value = "save")
    private ModelAndView getDrive(@RequestParam(required = false) Long id, @RequestParam(required = false) GameType type, @RequestParam(required = false, defaultValue = "false") boolean generate){
        return gameService.getById(id, type, generate);
    }

    @PostMapping(value="save")
    public RedirectView save(@RequestParam long id, @RequestParam List<String> questionIds, @RequestParam String advertisementIds, @RequestParam int duration, RedirectAttributes attributes){
        return gameService.updateBattleOrDrive(id, String.join(",", questionIds), advertisementIds, duration, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        gameService.deleteAllByIds(ids, attributes);
        return new RedirectView("/battle/list", true);
    }
}
