package com.estate.domain.service.impl;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Standing;
import com.estate.domain.enumaration.Level;
import com.estate.domain.form.StandingForm;
import com.estate.domain.service.face.StandingService;
import com.estate.repository.LogRepository;
import com.estate.repository.StandingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandingServiceImpl implements StandingService {
    private final StandingRepository standingRepository;
    private final LogRepository logRepository;

    @Override
    public List<Standing> findAll() {
        return standingRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Optional<Standing> findById(long id) {
        return standingRepository.findById(id);
    }

    @Override
    public RedirectView deleteAllByIdIn(List<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            standingRepository.deleteAllById(ids);
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des standings.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/standing/list", true);
    }

    @Override
    public Notification save(StandingForm form) {
        boolean creation = form.getId() == null;
        Notification notification = Notification.info();
        Standing standing = creation ? new Standing() : standingRepository.findById(form.getId()).orElse(null);
        if(standing == null) return Notification.error("Standing introuvable");
        standing.setName(form.getName());
        standing.setRent(form.getRent());
        standing.setCaution(form.getCaution());
        standing.setRepair(form.getRepair());

        try {
            standingRepository.saveAndFlush(standing);
            notification.setMessage("Un standing a été " + (creation ? "ajouté." : "modifié."));
            log.info(notification.getMessage());
        } catch (Throwable e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String message = ExceptionUtils.getRootCauseMessage(e);
            notification.setType(Level.ERROR);
            if(StringUtils.containsIgnoreCase(message, "UK_NAME")){
                notification.setMessage("Le <b>" + standing.getName() + "</b> standing existe déjà.");
            } else {
                notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " du standing.");
            }
            log.error(notification.getMessage(), e);
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        return notification;
    }
}
