package com.estate.domain.service.impl;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.*;
import com.estate.domain.enumaration.Level;
import com.estate.domain.enumaration.Status;
import com.estate.domain.form.PaymentForm;
import com.estate.domain.form.PaymentSearch;
import com.estate.domain.service.face.PaymentService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final StandingRepository standingRepository;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;
    private final HousingRepository housingRepository;
    private final LogRepository logRepository;

    @Override
    public Page<Payment> findAll(int page){
        return paymentRepository.findAllByOrderByCreationDateDesc(PageRequest.of(page  - 1, 500));
    }

    @Override
    public Page<Payment> findAll(PaymentSearch form) {
        return paymentRepository.findAll(form.toSpecification(), PageRequest.of(form.getPage()  - 1, 500, Sort.by(Sort.Direction.DESC, "creationDate")));
    }

    @Override
    public Optional<Payment> findById(long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public RedirectView deleteById(long id, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            paymentRepository.deleteAllByIdAndStatus(id, Status.CANCELLED);
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des recharges.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/recharge/list", true);
    }

    @Override
    public Notification toggle(long id, Status status) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if(payment == null) return Notification.error("Paiement introuvable");
        payment.setStatus(status);
        paymentRepository.saveAndFlush(payment);
        return Notification.info();
    }

    @Override
    public long countByStatus(Status status) {
        return paymentRepository.countAllByStatus(status);
    }

    @Override
    public Notification save(PaymentForm form) {
        boolean creation = form.getId() == null;
        Notification notification = Notification.info();
        Payment payment = creation ? new Payment() : paymentRepository.findById(form.getId()).orElse(null);
        if(payment == null) return Notification.error("Paiement introuvable");
        if(creation){
            Student student = studentRepository.findById(form.getStudentId()).orElse(null);
            if(student == null) return Notification.error("Étudiant introuvable");
            payment.setStudent(student);
            Standing standing = standingRepository.findById(form.getStandingId()).orElse(null);
            if(standing == null) return Notification.error("Standing introuvable");
            payment.setStanding(standing);
            Housing desiderata = housingRepository.findById(form.getDesiderataId()).orElse(null);
            if(desiderata == null) return Notification.error("Logement introuvable");
            payment.setDesiderata(desiderata);
            payment.setMonths(form.getMonths());
            payment.setRent(standing.getRent());
            payment.setCaution(standing.getCaution());
            payment.setRepair(standing.getRepair());
            payment.setMode(form.getMode());
        }

        try {
            paymentRepository.saveAndFlush(payment);
            notification.setMessage("Un paiement a été " + (creation ? "ajouté." : "modifié."));
            log.info(notification.getMessage());
        } catch (Throwable e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            notification.setType(Level.ERROR);
            notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " du paiement.");
            log.error(notification.getMessage(), e);
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        return notification;
    }
}
