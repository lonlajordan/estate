package com.estate.domain.service.impl;

import com.estate.domain.constant.SettingCode;
import com.estate.domain.dto.Notification;
import com.estate.domain.entity.*;
import com.estate.domain.enumaration.Operator;
import com.estate.domain.enumaration.Status;
import com.estate.domain.service.face.RechargeService;
import com.estate.repository.*;
import com.estate.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeServiceImpl implements RechargeService {
    @PersistenceContext
    private EntityManager em;
    private final RechargeRepository rechargeRepository;
    private final ProvinceRepository provinceRepository;
    private final SettingRepository settingRepository;
    private final LogRepository logRepository;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public Page<Recharge> findAll(int p){
        return rechargeRepository.findAllByOrderByDateDesc(PageRequest.of(p  - 1, 1000));
    }

    @Override
    public ModelAndView search(String name, String phone, Operator operator, Status status, Date start, Date end, int page){
        int pageSize = 1000;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Recharge> cq = cb.createQuery(Recharge.class);
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Recharge> recharge = cq.from(Recharge.class);
        List<Predicate> predicates = new ArrayList<>();
        ModelAndView view = new ModelAndView("admin/recharge/list");
        if(start.toInstant().getEpochSecond() > 0){
            predicates.add(cb.greaterThanOrEqualTo(recharge.get("date"), DateUtils.dateToLocalDateTime(start).with(LocalTime.MIN)));
            view.getModel().put("start", start);
        }
        if(end.toInstant().getEpochSecond() > 0){
            predicates.add(cb.lessThanOrEqualTo(recharge.get("date"), DateUtils.dateToLocalDateTime(end).with(LocalTime.MAX)));
            view.getModel().put("end", end);
        }
        if(StringUtils.isNotEmpty(name)){
            predicates.add(
                cb.or(
                    cb.like(cb.upper(recharge.get("player").get("firstName")), "%" + name.toUpperCase() + "%"),
                    cb.like(cb.lower(recharge.get("player").get("lastName")), "%" + name.toLowerCase() + "%")
                )
            );
            view.getModel().put("name", name);
        }
        if(StringUtils.isNotEmpty(phone)){
            predicates.add(cb.like(recharge.get("clientNumber"), "%" + phone + "%"));
            view.getModel().put("phone", phone);
        }
        if(operator != null){
            predicates.add(cb.equal(recharge.get("operator"), operator));
            view.getModel().put("operator", operator);
        }
        if(status != null){
            predicates.add(cb.equal(recharge.get("status"), status));
            view.getModel().put("status", status);
        }
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(recharge.get("date")));
        countQuery.select(cb.count(countQuery.where(predicates.toArray(new Predicate[0])).from(Recharge.class)));
        TypedQuery<Recharge> query = em.createQuery(cq).setMaxResults(pageSize).setFirstResult((page - 1) * pageSize);
        List<Recharge> recharges = query.getResultList();
        long count = em.createQuery(countQuery).getSingleResult();
        int totalPages = ((int) count / pageSize) + (count % pageSize == 0 ? 0 : 1);
        view.getModel().put("recharges", recharges);
        view.getModel().put("totalPages", totalPages);
        view.getModel().put("currentPage", page);
        view.getModel().put("search", true);
        return view;
    }

    @Override
    public ModelAndView statistics() {
        List<Province> provinces = provinceRepository.findAll();
        long[] result;
        long[] total = new long[]{0, 0, 0};
        int min = Calendar.getInstance().get(Calendar.YEAR) - settingRepository.findByCode(SettingCode.YOUTH_MAX_AGE).map(Setting::getValueAsInt).orElse(40) - 1;
        total[0] = rechargeRepository.sumAllAmountByPlayerBirthYearGreaterThan(min, Status.APPROVED);
        total[2] = rechargeRepository.sumAllAmount(Status.APPROVED);
        total[1] = total[2] - total[0];
        ModelAndView view = new ModelAndView("admin/recharge/statistic");
        view.getModel().put("provinces", provinces);
        view.getModel().put("total", total);
        return view;
    }



    @Override
    public Optional<Recharge> findById(long id) {
        return rechargeRepository.findById(id);
    }

    @Override
    public RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            rechargeRepository.deleteAllByIdInAndStatus(ids, Status.REJECTED);
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des recharges.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/recharge/list", true);
    }

    @Override
    public Notification status(long id) {
        return null;
    }
}
