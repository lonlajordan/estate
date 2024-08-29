package com.estate.domain.service.impl;

import com.estate.domain.entity.Lease;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Student;
import com.estate.domain.form.LeaseSearch;
import com.estate.domain.service.face.LeaseService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {
    private final HousingRepository housingRepository;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;
    private final LogRepository logRepository;

    @Override
    public Page<Lease> findAll(int page) {
        return leaseRepository.findAllByOrderByCreationDateDesc(PageRequest.of(page - 1, 100));
    }

    @Override
    public Page<Lease> findAll(LeaseSearch form) {
        return leaseRepository.findAll(form.toSpecification(), PageRequest.of(form.getPage() == null ? 0 : (form.getPage() - 1), 100, Sort.by(Sort.Direction.DESC, "creationDate")));
    }

    @Override
    public Optional<Lease> findById(long id) {
        return leaseRepository.findById(id);
    }

    @Override
    public long count() {
        return leaseRepository.count();
    }

    @Override
    public Notification toggleById(long id, Principal principal) {
        Notification notification = new Notification();
        Lease lease = leaseRepository.findById(id).orElse(null);
        if(lease == null) return Notification.error("Contrat de bail introuvable");
        try {
            if(lease.getStartDate() == null){
                LocalDate now = LocalDate.now();
                lease.setStartDate(now);
                lease.setEndDate(now.plusMonths(lease.getPayment().getMonths()));
                lease.setHousing(lease.getPayment().getDesiderata());
            }
            lease = leaseRepository.save(lease);
            Student student = lease.getPayment().getStudent();
            Lease currentLease  = student.getCurrentLease();
            if(currentLease != null){
                currentLease.setNextLease(lease);
                leaseRepository.save(currentLease);
            }
            notification.setMessage("Le contract de bail de l'étudiant <b>" + lease.getPayment().getStudent().getName() + "</b> a été activé avec succès.");
            logRepository.save(Log.info(notification.getMessage()).author(Optional.ofNullable(principal).map(Principal::getName).orElse("")));
        } catch (Throwable e){
            notification = Notification.error("Erreur lors de l'activation du contrat de bail.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)).author(Optional.ofNullable(principal).map(Principal::getName).orElse("")));
        }
        return notification;
    }
}
