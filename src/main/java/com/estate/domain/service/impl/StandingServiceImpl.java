package com.estate.domain.service.impl;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Standing;
import com.estate.domain.enumaration.Level;
import com.estate.domain.form.StandingForm;
import com.estate.domain.service.face.StandingService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandingServiceImpl implements StandingService {
    private final StandingRepository standingRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;
    private final HousingRepository housingRepository;
    private final LogRepository logRepository;

    @Override
    public List<Standing> findAll() {
        return standingRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<Standing> findAllByActiveTrue() {
        return standingRepository.findAllByActiveTrueOrderByNameAsc();
    }


    @Override
    public Optional<Standing> findById(long id) {
        return standingRepository.findById(id);
    }

    @Override
    public Notification deleteById(long id, boolean force, HttpServletRequest request){
        Notification notification;
        Standing standing = standingRepository.findById(id).orElse(null);
        if(standing == null) return Notification.error("Standing introuvable");
        try {
            if(force){
                leaseRepository.deleteAllByPaymentStandingId(id);
                paymentRepository.deleteAllByStandingId(id);
                housingRepository.deleteAllByStandingId(id);
            }
            standingRepository.deleteById(id);
            notification = Notification.info("Le <b>" + standing.getName() + "</b> standing a été supprimé");
            logRepository.save(Log.info(notification.getMessage()));
        }catch (Throwable e){
            notification = Notification.error("Erreur lors de la suppression du <b>" + standing.getName() + "</b> standing.");
            if(!force){
                String actions = "";
                if(standing.isActive()) actions = "<a class='lazy-link' href='" + request.getContextPath() + "/standing/toggle/" + id + "'><b>Désactiver</b></a> ou ";
                actions += "<a class='lazy-link text-danger' href='" + request.getRequestURI() + "?id=" + id + "&force=true" + "'><b>Forcer la suppression</b></a> (cette action supprimera tout logement, paiement ou contrat de bail associé).";
                notification = Notification.warn("Ce standing est utilisé dans certains enregistrements. " + actions);
                logRepository.save(Log.warn(notification.getMessage()));
            } else {
                logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
            }
        }
        return notification;
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
            logRepository.save(Log.info(notification.getMessage()));
        } catch (Throwable e){
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

    @Override
    public Notification toggleById(long id) {
        Notification notification = new Notification();
        Standing standing = standingRepository.findById(id).orElse(null);
        if(standing == null) return Notification.error("Standing introuvable");
        try {
            standing.setActive(!standing.isActive());
            standingRepository.save(standing);
            notification.setMessage("Le <b>" + standing.getName() + "</b> standing a été " + (standing.isActive() ? "activé" : "désactivé") + " avec succès.");
            logRepository.save(Log.info(notification.getMessage()));
        } catch (Throwable e){
            notification = Notification.error("Erreur lors du changement de statut du <b>" + standing.getName() + "</b> standing.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return notification;
    }
}
