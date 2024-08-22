package com.estate.domain.service.impl;

import com.estate.domain.entity.Housing;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Notification;
import com.estate.domain.enumaration.Level;
import com.estate.domain.form.HousingForm;
import com.estate.domain.form.HousingSearch;
import com.estate.domain.service.face.HousingService;
import com.estate.repository.HousingRepository;
import com.estate.repository.LogRepository;
import com.estate.repository.StandingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HousingServiceImpl implements HousingService {
    private final HousingRepository housingRepository;
    private final StandingRepository standingRepository;
    private final LogRepository logRepository;

    @Override
    public List<Housing> findAll() {
        return housingRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<Housing> findAll(HousingSearch form) {
        return housingRepository.findAll(form.toSpecification(), Sort.by(Sort.Direction.ASC, "name"));
    }

    @Override
    public Optional<Housing> findById(long id) {
        return housingRepository.findById(id);
    }

    @Override
    public long count() {
        return housingRepository.count();
    }

    @Override
    @Transactional
    public Notification save(HousingForm form) {
        boolean creation = form.getId() == null;
        Notification notification = Notification.info();
        Housing housing = creation ? new Housing() : housingRepository.findById(form.getId()).orElse(null);
        if(housing == null) return Notification.error("Logement introuvable");
        housing.setName(form.getName());
        housing.setStanding(standingRepository.getReferenceById(form.getStandingId()));
        housing.setStatus(form.getStatus());

        try {
            housingRepository.saveAndFlush(housing);
            notification.setMessage("Un logement a été " + (creation ? "ajouté." : "modifié."));
            log.info(notification.getMessage());
        } catch (Throwable e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String message = ExceptionUtils.getRootCauseMessage(e);
            notification.setType(Level.ERROR);
            if(StringUtils.containsIgnoreCase(message, "UK_NAME")){
                notification.setMessage("Le logement <b>" + housing.getName() + "</b> existe déjà.");
            } else {
                notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " du logement.");
            }
            log.error(notification.getMessage(), e);
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        return notification;
    }

    @Override
    public RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes) {
        return null;
    }
}
