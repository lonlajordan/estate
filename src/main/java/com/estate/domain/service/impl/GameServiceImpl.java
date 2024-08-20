package com.estate.domain.service.impl;


import com.estate.domain.constant.SettingCode;
import com.estate.domain.dto.Notification;
import com.estate.domain.entity.*;
import com.estate.domain.enumaration.GameType;
import com.estate.domain.enumaration.Level;
import com.estate.domain.exception.NotFoundException;
import com.estate.domain.mail.EmailHelper;
import com.estate.domain.service.face.GameService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Hibernate;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final QuestionRepository questionRepository;
    private final AdvertisementRepository advertisementRepository;
    private final SettingRepository settingRepository;
    private final ProvinceRepository provinceRepository;

    public final EmailHelper emailHelper;
    private final LogRepository logRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public ModelAndView getById(Long id, GameType type, boolean generate) {
        Game game = new Game();
        if(id != null) game = gameRepository.findById(id).orElse(new Game());
        if(type != null) game.setType(type);
        String viewName = "admin/quiz/save";
        List<List<Long>> series;
        List<String> questionIds;
        String advertisementIds;
        int countQuestions = 0, countAdvertisements = 0, duration = 30;
        switch (game.getType()){
            case QUIZ:
                countQuestions = settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_QUESTION_COUNT).map(Setting::getValueAsInt).orElse(10);
                countAdvertisements =  settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_ADVERTISEMENT_COUNT).map(Setting::getValueAsInt).orElse(2);
                duration =  settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_DURATION).map(Setting::getValueAsInt).orElse(25);
                break;
            case MEGA_QUIZ:
                countQuestions = settingRepository.findByCode(SettingCode.MEGA_QUIZ_QUESTION_COUNT).map(Setting::getValueAsInt).orElse(40);
                countAdvertisements = settingRepository.findByCode(SettingCode.MEGA_QUIZ_ADVERTISEMENT_COUNT).map(Setting::getValueAsInt).orElse(2);
                duration = settingRepository.findByCode(SettingCode.MEGA_QUIZ_DURATION).map(Setting::getValueAsInt).orElse(90);
                break;
            case BATTLE:
                countQuestions = settingRepository.findByCode(SettingCode.BATTLE_QUESTION_COUNT).map(Setting::getValueAsInt).orElse(20);
                countAdvertisements = settingRepository.findByCode(SettingCode.BATTLE_ADVERTISEMENT_COUNT).map(Setting::getValueAsInt).orElse(2);
                duration = settingRepository.findByCode(SettingCode.BATTLE_DURATION).map(Setting::getValueAsInt).orElse(45);
                viewName = "admin/battle/save";
                break;
            case DRIVE_TEST:
                countQuestions = settingRepository.findByCode(SettingCode.DRIVE_TEST_QUESTION_COUNT).map(Setting::getValueAsInt).orElse(20);
                countAdvertisements = settingRepository.findByCode(SettingCode.DRIVE_TEST_ADVERTISEMENT_COUNT).map(Setting::getValueAsInt).orElse(2);
                duration = settingRepository.findByCode(SettingCode.DRIVE_TEST_DURATION).map(Setting::getValueAsInt).orElse(45);
                viewName = "admin/drive/save";
                break;
            default:
                break;
        }
        int n = (int) Math.ceil(countQuestions / 10.0);
        ModelAndView view = new ModelAndView(viewName);
        if(game.getId() == null) game.setDuration(duration);
        view.getModel().put("game", game);
        if(generate){
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Question> cq = cb.createQuery(Question.class);
            Root<Question> order = cq.from(Question.class);
            cq.where(cb.isFalse(order.get("deleted")), cb.isTrue(order.get("uploaded")));
            cq.orderBy(cb.asc(cb.function("RAND", null)));
            TypedQuery<Question> query = em.createQuery(cq).setMaxResults(countQuestions).setFirstResult(0);
            series = ListUtils.partition(query.getResultList().stream().map(Question::getId).sorted(Long::compare).collect(Collectors.toList()), 10);
            questionIds = series.stream().map(s -> s.stream().map(String::valueOf).collect(Collectors.joining(","))).collect(Collectors.toList());

            Page<Advertisement> advertisements = advertisementRepository.findAllByOrderByViewsAsc(PageRequest.of(0, countAdvertisements));
            advertisementIds = advertisements.stream().map(Advertisement::getId).map(String::valueOf).collect(Collectors.joining(","));
        }else{
            series = ListUtils.partition(game.getQuestions().stream().map(Question::getId).collect(Collectors.toList()), 10);
            questionIds = series.stream().map(s -> s.stream().map(String::valueOf).collect(Collectors.joining(","))).collect(Collectors.toList());
            advertisementIds = game.getAdvertisements().stream().map(Advertisement::getId).map(String::valueOf).collect(Collectors.joining(","));
        }
        questionIds.addAll(Collections.nCopies(Math.max(0, n - questionIds.size()), ""));
        view.getModel().put("questionIds", questionIds);
        view.getModel().put("advertisementIds", advertisementIds);
        view.getModel().put("creation", game.getId() == null);
        return view;
    }

    @Override
    public Page<Game> findAllByTypeIn(List<GameType> types, int p) {
        return gameRepository.findAllByTypeInOrderByDateDesc(types, PageRequest.of(p  - 1, 500));
    }


    @Override
    public long countGameByType(GameType gameType){
        return gameRepository.countAllByType(gameType);
    }

    @Override
    public void findParticipation(long id, Model model) {

    }


    @Override
    public RedirectView updateBattleOrDrive(long gameId, String questionIds, String advertisementIds, int duration, RedirectAttributes attributes) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException(Game.class, gameId));
        Set<Long> _questionIds = Arrays.stream(questionIds.split(",")).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        Set<Long> _advertisementIds = Arrays.stream(advertisementIds.split(",")).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        List<Question> questions = questionRepository.findAllByIdIn(_questionIds);
        List<Advertisement> advertisements = advertisementRepository.findAllByIdIn(_advertisementIds);
        game.setQuestions(questions);
        game.setAdvertisements(advertisements);
        game.setDuration(duration);
        Notification notification = new Notification();
        try {
            gameRepository.save(game);
            notification.setMessage("Le test de conduite a été modifié.");
            logRepository.save(Log.info(notification.getMessage()));
        } catch (Exception e){
            notification.setType(Level.ERROR);
            notification.setMessage("Erreur lors de la modification du test de conduite.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView(GameType.BATTLE.equals(game.getType()) ? "/battle/list" : "/drive/list", true);
    }

    @Override
    public ModelAndView createOrUpdate(Game game, String questionIds, String advertisementIds, Boolean multiple, RedirectAttributes attributes) {
        Game game$ = game;
        if(game.getId() != null){
            Optional<Game> _game = gameRepository.findById(game.getId());
            if(_game.isPresent()){
                game$ = _game.get();
                game$.setType(game.getType());
                game$.setDate(game.getDate());
                game$.setDuration(game.getDuration());
            }
        }
        Set<Long> _questionIds = Arrays.stream(questionIds.split(",")).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        Set<Long> _advertisementIds = Arrays.stream(advertisementIds.split(",")).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
        Notification notification = new Notification();
        int countQuestions = 0, countAdvertisements = 0, duration = 30;
        if(GameType.QUIZ.equals(game$.getType())){
            LocalDateTime today = LocalDateTime.now();
            boolean isBeforeWednesday = today.getDayOfWeek().getValue() < 3;
            game$.setDate(today.with(TemporalAdjusters.nextOrSame(isBeforeWednesday ? DayOfWeek.TUESDAY : DayOfWeek.THURSDAY)).withHour(isBeforeWednesday ? 16 : 18).withMinute(0).withSecond(0).withNano(0));
            if(game$.getId() == null){
                notification.setMessage("Un quiz ordinaire a déjà été programmé pour cette semaine");
                notification.setType(Level.ERROR);
            }else{
                countQuestions = settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_QUESTION_COUNT).map(Setting::getValueAsInt).orElse(10);
                countAdvertisements =  settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_ADVERTISEMENT_COUNT).map(Setting::getValueAsInt).orElse(2);
                duration =  settingRepository.findByCode(SettingCode.ORDINARY_QUIZ_DURATION).map(Setting::getValueAsInt).orElse(25);
            }
        }else if(GameType.MEGA_QUIZ.equals(game$.getType())){
            countQuestions = settingRepository.findByCode(SettingCode.MEGA_QUIZ_QUESTION_COUNT).map(Setting::getValueAsInt).orElse(40);
            countAdvertisements = settingRepository.findByCode(SettingCode.MEGA_QUIZ_ADVERTISEMENT_COUNT).map(Setting::getValueAsInt).orElse(2);
            duration = settingRepository.findByCode(SettingCode.MEGA_QUIZ_DURATION).map(Setting::getValueAsInt).orElse(90);
        }
        List<Question> questions = questionRepository.findAllByIdIn(_questionIds).stream().limit(countQuestions).collect(Collectors.toList());
        List<Advertisement> advertisements = advertisementRepository.findAllByIdIn(_advertisementIds).stream().limit(countAdvertisements).collect(Collectors.toList());
        if(!Level.ERROR.equals(notification.getType())){
            game$.setQuestions(questions);
            game$.setAdvertisements(advertisements);
            try {
                notification.setMessage("Le jeu a été " + (game$.getId() == null ? "ajouté." : "modifié."));
                logRepository.save(Log.info(notification.getMessage()));
                gameRepository.save(game$);
                GameType type = game$.getType();
                game$ = new Game();
                game$.setType(type);
                game$.setDuration(duration);
                _questionIds = new HashSet<>();
                advertisementIds = "";
            } catch (Exception e){
                notification.setType(Level.ERROR);
                notification.setMessage("Erreur lors de la " + (game$.getId() == null ? "création" : "modification") + " du jeu.");
                logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
            }
        }

        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            int n = (int) Math.ceil(countQuestions / 10.0);
            List<List<Long>> series = ListUtils.partition(new ArrayList<>(_questionIds), 10);
            List<String> ids = series.stream().map(s -> s.stream().map(String::valueOf).collect(Collectors.joining(","))).collect(Collectors.toList());
            ids.addAll(Collections.nCopies(Math.max(0, n - ids.size()), ""));
            view.setViewName("admin/quiz/save");
            view.getModel().put("game", game$);
            view.getModel().put("questionIds", ids);
            view.getModel().put("advertisementIds", advertisementIds);
            view.getModel().put("creation", game$.getId() == null);
            view.getModel().put("notification", notification);
        }else{
            view.setViewName("redirect:/quiz/list");
            attributes.addFlashAttribute("notification", notification);
        }
        return view;
    }

    @Transactional
    @Override
    public ModelAndView gameDetails(long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new NotFoundException(Game.class, id));
        Hibernate.initialize(game.getQuestions());
        Hibernate.initialize(game.getAdvertisements());
        ModelAndView view = new ModelAndView("admin/quiz/view");
        view.getModel().put("game", game);
        List<Province> provinces = provinceRepository.findAll();
        List<List<long[]>> participations = new ArrayList<>();
        List<long[]> totals = new ArrayList<>();
        long[] result;
        long[] total = new long[]{0, 0, 0};

        view.getModel().put("provinces", provinces);
        view.getModel().put("totals", totals);
        view.getModel().put("total", total);
        view.getModel().put("participations", participations);

        return view;
    }

    @Override
    public ModelAndView statisticSearch(long id, int page, Long provinceId, Long cityId, Character category) {
        return null;
    }

    @Override
    public void driveReport(long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new NotFoundException(Game.class, id));
        if(GameType.DRIVE_TEST.equals(game.getType())){
            String fileName = "Résultat_" + id + ".pdf";

            List<String> attachment = new ArrayList<>();
            attachment.add(fileName);
            String subject = "RÉSULTAT DU TEST CONDUCTEUR";
            Map<String, Object> model = new HashMap<>();
            model.put("title",game.getTitle());
            model.put("name", game.getInitiator().getName());
            emailHelper.sendMail(game.getInitiator().getEmail(),"", subject, "drive_test_results.ftl", game.getInitiator().getLocale(), model, attachment);
        }
    }

    @Transactional
    @Override
    public ModelAndView driveDetails(long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new NotFoundException(Game.class, id));
        Hibernate.initialize(game.getQuestions());
        Hibernate.initialize(game.getAdvertisements());
        ModelAndView view = new ModelAndView("admin/drive/view");
        view.getModel().put("drive", game);
        return view;
    }

    @Transactional
    @Override
    public ModelAndView getBattleDetails(long battleId){
        ModelAndView view = new ModelAndView("admin/battle/view");
        Game game = gameRepository.findById(battleId).orElseThrow(() -> new NotFoundException(Game.class, battleId));
        Hibernate.initialize(game.getQuestions());
        Hibernate.initialize(game.getAdvertisements());
        view.getModel().put("game", game);
        return view;
    }


    public void generateQuestionsAndAdvertisements(Game game, int countQuestions, int countAdvertisements){
        if(countQuestions > 0){
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Question> cq = cb.createQuery(Question.class);
            Root<Question> order = cq.from(Question.class);
            cq.where(cb.isFalse(order.get("deleted")), cb.isTrue(order.get("uploaded")));
            cq.orderBy(cb.asc(cb.function("RAND", null)));
            TypedQuery<Question> query = em.createQuery(cq).setMaxResults(countQuestions).setFirstResult(0);
            game.setQuestions(query.getResultList());
        }
        if(countAdvertisements > 0){
            Page<Advertisement> advertisements = advertisementRepository.findAllByOrderByViewsAsc(PageRequest.of(0, countAdvertisements));
            game.setAdvertisements(advertisements.toList());
        }
        if(countQuestions > 0 || countAdvertisements > 0) game = gameRepository.save(game);
    }


    @Override
    public void deleteAllByIds(List<Long> ids, RedirectAttributes attributes) {
        Notification notification = Notification.info();
        try {
            gameRepository.deleteAllById(ids);
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des jeux.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
    }
}
