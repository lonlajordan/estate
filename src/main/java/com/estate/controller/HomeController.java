package com.estate.controller;

import com.estate.domain.enumaration.GameType;
import com.estate.domain.service.face.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final GameService gameService;
    private final PlayerService playerService;
    private final UserService userService;
    private final QuestionService questionService;
    private final MysteryService mysteryService;
    private final AdvertisementService advertisementService;

    @GetMapping("home")
    private String home(Model model){
        model.addAttribute("players", playerService.countPlayers());
        model.addAttribute("users", userService.countUsers());
        model.addAttribute("mysteries", mysteryService.countMysteries());
        model.addAttribute("questions", questionService.countQuestions());
        model.addAttribute("quizzes", gameService.countGameByType(GameType.QUIZ));
        model.addAttribute("advertisements", advertisementService.countAdvertisements());
        return "index";
    }
}