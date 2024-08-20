package com.estate.domain.service.impl;

import com.estate.domain.constant.SettingCode;
import com.estate.domain.dto.Notification;
import com.estate.domain.entity.*;
import com.estate.domain.service.face.PlayerService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    private final LogRepository logRepository;
    private final SettingRepository settingRepository;

    @PersistenceContext
    private EntityManager em;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public long countPlayers(){
        return playerRepository.countAllByDeletedFalse();
    }

    @Override
    public Optional<Player> findById(long id) {
        return playerRepository.findById(id);
    }

    @Override
    public void deleteById(long id) {
        playerRepository.findById(id).ifPresent(playerRepository::delete);
    }


    public Player save(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public ModelAndView playerList(int p) {
        Pageable pageable = PageRequest.of(p  - 1, 500);
        Page<Player> players = playerRepository.findAllByOrderByCreatedAtDesc(pageable);
        ModelAndView view = new ModelAndView("admin/player/list");
        view.getModel().put("players", players.toList());
        view.getModel().put("provinces", provinceRepository.findAllByOrderByNameAsc());
        view.getModel().put("cities", cityRepository.findAllByOrderByNameAsc());
        view.getModel().put("totalPages", players.getTotalPages());
        view.getModel().put("currentPage", p);
        view.getModel().put("search", false);
        return view;
    }

    @Override
    public ModelAndView search(int page, String firstName, String lastName, String pseudo, Integer birthYear, Long provinceId, Long cityId, String phoneNumber, String email, Character sex, int status) {
        int pageSize = 500;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Player> cq = cb.createQuery(Player.class);
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Player> order = cq.from(Player.class);
        List<Predicate> predicates = new ArrayList<>();
        if(StringUtils.isNotEmpty(firstName)) predicates.add(cb.like(cb.upper(order.get("firstName")), "%" + firstName.toUpperCase() + "%"));
        if(StringUtils.isNotEmpty(lastName)) predicates.add(cb.like(cb.lower(order.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        if(StringUtils.isNotEmpty(pseudo)) predicates.add(cb.like(cb.lower(order.get("pseudo")), "%" + pseudo.toLowerCase() + "%"));
        if(status > -1) predicates.add(cb.equal(order.get("enabled"), status == 1));
        if(StringUtils.isNotBlank(sex + "")) predicates.add(cb.equal(order.get("sex"), sex));
        if(birthYear > 0) predicates.add(cb.equal(order.get("birthYear"), birthYear));
        if(provinceId > 0) predicates.add(cb.equal(order.get("city").get("province").get("id"), provinceId));
        if(cityId > 0) predicates.add(cb.equal(order.get("city").get("id"), cityId));
        if(StringUtils.isNotEmpty(phoneNumber)) predicates.add(cb.like(order.get("phoneNumber"), "%" + phoneNumber + "%"));
        if(StringUtils.isNotEmpty(email)) predicates.add(cb.like(cb.lower(order.get("email")), "%" + email.toLowerCase() + "%"));
        cq.where(predicates.toArray(new Predicate[0]));
        countQuery.select(cb.count(countQuery.where(predicates.toArray(new Predicate[0])).from(Player.class)));
        TypedQuery<Player> query = em.createQuery(cq).setMaxResults(pageSize).setFirstResult((page - 1) * pageSize);
        List<Player> players = query.getResultList();
        ModelAndView view = new ModelAndView("admin/player/list");
        view.getModel().put("players", players);
        view.getModel().put("provinces", provinceRepository.findAllByOrderByNameAsc());
        view.getModel().put("cities", cityRepository.findAllByOrderByNameAsc());
        long count = em.createQuery(countQuery).getSingleResult();
        int totalPages = ((int) count / pageSize) + (count % pageSize == 0 ? 0 : 1);
        view.getModel().put("totalPages", totalPages);
        view.getModel().put("currentPage", page);

        view.getModel().put("firstName", firstName);
        view.getModel().put("lastName", lastName);
        view.getModel().put("pseudo", pseudo);
        if(birthYear > 0) view.getModel().put("birthYear", birthYear);
        view.getModel().put("provinceId", provinceId);
        view.getModel().put("cityId", cityId);
        view.getModel().put("phoneNumber", phoneNumber);
        view.getModel().put("email", email);
        view.getModel().put("sex", sex);
        view.getModel().put("status", status);
        view.getModel().put("search", true);
        return view;
    }

    @Override
    public ModelAndView playersDetails() {
        List<Province> provinces = provinceRepository.findAll();
        List<List<long[]>> players = new ArrayList<>();
        List<long[]> totals = new ArrayList<>();
        long[] result;
        long[] total = new long[]{0, 0, 0};
        int min = Calendar.getInstance().get(Calendar.YEAR) - settingRepository.findByCode(SettingCode.YOUTH_MAX_AGE).map(Setting::getValueAsInt).orElse(40) - 1;
        total[0] = playerRepository.countAllByBirthYearGreaterThan(min);
        total[2] = playerRepository.count();
        total[1] = total[2] - total[0];
        for(Province province : provinces){
            List<long[]> results = new ArrayList<>();
            result = new long[]{0, 0, 0};
            result[1] = result[2] - result[0];
            totals.add(result);
            players.add(results);
        }
        ModelAndView view = new ModelAndView("admin/player/statistic");
        view.getModel().put("provinces", provinces);
        view.getModel().put("totals", totals);
        view.getModel().put("total", total);
        view.getModel().put("players", players);
        return view;
    }

    @Override
    public RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes){
        for(Long id: ids) deleteById(id);
        attributes.addFlashAttribute("notification", Notification.info());
        return new RedirectView("/player/list", true);
    }

    @Override
    public RedirectView renameById(long id, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            Player player = playerRepository.findById(id).orElse(null);
            if(player != null){
                String fistName = player.getFirstName();
                player.setFirstName(player.getLastName());
                player.setLastName(fistName);
                player = playerRepository.save(player);
                notification.setMessage("<b>" + player.getName() + "</b> a été renommé avec succès.");
                logRepository.save(Log.info(notification.getMessage()));
            }
        }catch (Exception e){
            notification = Notification.error("Erreur lors du renommage du joueur d'identifiant <b>" + id + "</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/player/list", true);
    }

    @Override
    public RedirectView toggleById(long id, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            Player player = playerRepository.findById(id).orElse(null);
            if(player != null){
                player.setEnabled(!player.getEnabled());
                playerRepository.save(player);
                notification.setMessage("<b>" + player.getName() + "</b> a été " + (player.getEnabled() ? "activé" : "désactivé") + " avec succès.");
                logRepository.save(Log.info(notification.getMessage()));
            }
        }catch (Exception e){
            notification = Notification.error("Erreur lors du changement de statut du joueur d'identifiant <b>" + id + "</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/player/list", true);
    }
}
