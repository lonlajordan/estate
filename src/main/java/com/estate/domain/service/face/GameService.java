package com.estate.domain.service.face;

import com.estate.domain.entity.Game;
import com.estate.domain.enumaration.GameType;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.FileNotFoundException;
import java.util.List;

public interface GameService {

    Page<Game> findAllByTypeIn(List<GameType> quiz, int p);

    ModelAndView gameDetails(long id);

    ModelAndView statisticSearch(long id, int page, Long provinceId, Long cityId, Character category);

    ModelAndView getById(Long id, GameType type, boolean generate);

    ModelAndView createOrUpdate(Game game, String join, String advertisementIds, Boolean multiple, RedirectAttributes attributes);

    void deleteAllByIds(List<Long> ids, RedirectAttributes attributes);

    ModelAndView getBattleDetails(long id);

    long countGameByType(GameType gameType);

    void findParticipation(long id, Model model);

    ModelAndView driveDetails(long id);

    void driveReport(long id) throws FileNotFoundException;

    RedirectView updateBattleOrDrive(long id, String join, String advertisementIds, int duration, RedirectAttributes attributes);


    void generateQuestionsAndAdvertisements(Game game, int countQuestions, int countAdvertisements);
}
