package com.estate.controller;

import com.estate.domain.entity.Player;
import com.estate.domain.service.face.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping(value="list")
    public ModelAndView getAll(@RequestParam(required = false, defaultValue = "1") int p){
        return playerService.playerList(p);
    }

    @GetMapping(value="view/{id}")
    public String view(@PathVariable long id, Model model){
        Optional<Player> player = playerService.findById(id);
        model.addAttribute("player", player.orElse(new Player()));
        model.addAttribute("creation", player.isEmpty());
        return "admin/player/view";
    }

    @RequestMapping(value="rename/{id}")
    public RedirectView renamePlayer(@PathVariable int id, RedirectAttributes attributes){
        return playerService.renameById(id, attributes);
    }

    @RequestMapping(value="toggle/{id}")
    public RedirectView togglePlayer(@PathVariable int id, RedirectAttributes attributes){
        return playerService.toggleById(id, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        return playerService.deleteAllByIds(ids, attributes);
    }

    @PostMapping(value="search")
    public ModelAndView search(@RequestParam(required = false, defaultValue = "1") int page, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String pseudo, @RequestParam(required = false, defaultValue = "-1") Integer birthYear, @RequestParam(required = false, defaultValue = "-1") Long provinceId, @RequestParam(required = false, defaultValue = "-1") Long cityId, @RequestParam String phoneNumber, @RequestParam String email, @RequestParam Character sex, @RequestParam(required = false, defaultValue = "-1") int status){
        return playerService.search(page, firstName, lastName, pseudo, birthYear, provinceId, cityId, phoneNumber, email, sex, status);
    }

    @GetMapping(value="statistic")
    public ModelAndView statistics(){
        return playerService.playersDetails();
    }
}
