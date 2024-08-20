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

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {
    private final GameService gameService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Game> games = gameService.findAllByTypeIn(List.of(GameType.QUIZ, GameType.MEGA_QUIZ), p);
        model.addAttribute("games", games.toList());
        model.addAttribute("totalPages", games.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/quiz/list";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView view(@PathVariable long id){
        return gameService.gameDetails(id);
    }


    @PostMapping(value="statistic/{id}/search")
    public ModelAndView search(@PathVariable long id, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "-1") Long provinceId, @RequestParam(required = false, defaultValue = "-1") Long cityId, @RequestParam Character category){
        return gameService.statisticSearch(id, page, provinceId, cityId, category);
    }

    @GetMapping(value = "save")
    private ModelAndView getQuiz(@RequestParam(required = false) Long id, @RequestParam(required = false) GameType type, @RequestParam(required = false, defaultValue = "false") boolean generate){
        return gameService.getById(id, type, generate);
    }

    @PostMapping(value="save")
    public ModelAndView save(@Valid Game game, @RequestParam List<String> questionIds, @RequestParam String advertisementIds, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        return gameService.createOrUpdate(game, String.join(",", questionIds), advertisementIds, multiple, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam List<Long> ids, RedirectAttributes attributes){
        gameService.deleteAllByIds(ids, attributes);
        return new RedirectView("/quiz/list", true);
    }
}
