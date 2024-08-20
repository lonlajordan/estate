package com.estate.configuration;

import com.estate.domain.constant.MessageCode;
import com.estate.domain.constant.SettingCode;
import com.estate.domain.dto.Note;
import com.estate.domain.entity.*;
import com.estate.domain.enumaration.GameType;
import com.estate.domain.enumaration.Status;
import com.estate.domain.service.face.GameService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.transaction.Transactional;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {
    private final AdvertisementRepository advertisementRepository;
    private final GameService gameService;
    private final GameRepository gameRepository;
    private final QuestionRepository questionRepository;
    private final SettingRepository settingRepository;
    private final InvitationRepository invitationRepository;

    private final MessageSource messageSource;

    @Value("${app.base.url}")
    private String baseUrl;

    @Scheduled(cron = "0 0 1 ? * WED,SUN")
    public void generateOrdinaryQuiz(){
        int duration = settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_DURATION).map(Setting::getValueAsInt).orElse(30);
        int countQuestions = settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_QUESTION_COUNT).map(Setting::getValueAsInt).orElse(10);
        int countAdvertisements = settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_ADVERTISEMENT_COUNT).map(Setting::getValueAsInt).orElse(2);
        for(int i = 0; i < 2; i++){
            LocalDateTime dateTime = LocalDateTime.now().with(TemporalAdjusters.next(i == 0 ? DayOfWeek.TUESDAY : DayOfWeek.THURSDAY)).withHour(i == 0 ? 16 : 18).withMinute(0).withSecond(0).withNano(0);
            if(gameRepository.countAllByTypeAndDateBetween(GameType.QUIZ, dateTime.minusMinutes(5L), dateTime.plusMinutes(5L)) > 0) continue;
            Game game = new Game();
            game.setType(GameType.QUIZ);
            game.setDate(dateTime);
            game.setDuration(duration);
            gameService.generateQuestionsAndAdvertisements(game, countQuestions, countAdvertisements);
            Locale language = RandomUtils.nextBoolean() ? Locale.ENGLISH : Locale.FRENCH;
            Note note = new Note();
            note.setSubject(messageSource.getMessage(MessageCode.GAME_INITIATED_TITLE, null, language));
            note.setContent(messageSource.getMessage(MessageCode.GAME_INITIATED_MESSAGE, null, language));
            note.setImage(baseUrl + "/images/logo.png");
        }
    }


    @Scheduled(cron = "0 0/15 * * * ?")
    @Transactional
    public void closeGames(){
        LocalDateTime now = LocalDateTime.now();
        List<Game> games = gameRepository.findAllByClosedFalseAndEndingBefore(now.plusSeconds(1));
        for(Game game : games){
            List<Advertisement> advertisements = game.getAdvertisements();
            advertisements.forEach(advertisement -> advertisement.setViews(advertisement.getViews() + 1));
            advertisementRepository.saveAll(advertisements);
            if(GameType.BATTLE.equals(game.getType())) invitationRepository.deleteAllByGameIdAndStatus(game.getId(), Status.PENDING);
        }
    }

    @Async
    public void uploadLocalQuestionVideo(List<Question> questions){
        if(questions.isEmpty()) questions = questionRepository.findAllByUploadedFalse();
        File folder = new File("questions");
        if (!folder.exists() && !folder.mkdirs()) throw new SecurityException("Erreur lors de la création du dossier de sauvegarde des questions.");
        long targetSize = Long.parseLong(settingRepository.findByCode(SettingCode.VIDEO_COMPRESSION_TARGET_SIZE).map(Setting::getValue).orElse("3")) * 1024 * 1024;
        for(Question question: questions){
            String formulationFileName = "questions/" + question.getFormulationFileName();
            String justificationFileName = "questions/" + question.getJustificationFileName();
            File formulation = new File(folder.getAbsolutePath() + File.separator + question.getFormulationFileName());
            File justification = new File(folder.getAbsolutePath() + File.separator + question.getJustificationFileName());

            try {
                if(formulation.exists()){
                    question.setFormulationSize(formulation.length());
                    question.setUploaded(true);
                    FileUtils.deleteQuietly(formulation);
                }
            }catch (Exception ignored) {}
            try {
                if(justification.exists()){
                    question.setJustificationSize(justification.length());
                    FileUtils.deleteQuietly(justification);
                }
            }catch (Exception ignored) {}
            question.setFormulationFileName(formulationFileName);
            question.setJustificationFileName(justificationFileName);
            questionRepository.save(question);
        }
    }
}
