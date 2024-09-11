package com.estate.domain.service.impl;

import com.estate.domain.entity.*;
import com.estate.domain.enumaration.Availability;
import com.estate.domain.form.LeaseSearch;
import com.estate.domain.form.MutationForm;
import com.estate.domain.service.face.LeaseService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {
    private final TemplateEngine templateEngine;
    private final HousingRepository housingRepository;
    private final StudentRepository studentRepository;
    private final LeaseRepository leaseRepository;
    private final LogRepository logRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Lease> findAll(int page) {
        return leaseRepository.findAllByOrderByCreationDateDesc(PageRequest.of(page - 1, 100));
    }

    @Override
    public Page<Lease> findAll(LeaseSearch form) {
        return leaseRepository.findAll(form.toSpecification(), PageRequest.of(form.getPage() == null ? 0 : (form.getPage() - 1), 100, Sort.by(Sort.Direction.DESC, "creationDate")));
    }

    @Override
    public Page<Lease> findAllByUserId(long userId, int page) {
        return leaseRepository.findAllByPaymentStudentUserIdOrderByCreationDateDesc(userId, PageRequest.of(page - 1, 100));
    }

    @Override
    public Optional<Lease> findById(long id) {
        return leaseRepository.findById(id);
    }

    @Override
    public ResponseEntity<?> download(long id) {
        Lease lease = findById(id).orElse(null);
        Context context = new Context();
        context.setVariable("lease", lease);
        String html = templateEngine.process("contract", context);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(stream);
        String fileName = "contract.pdf";
        MediaType contentType = MediaType.APPLICATION_PDF;
        HttpHeaders header = new HttpHeaders();
        header.setContentType(contentType);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()), header, HttpStatus.OK);
    }

    @Override
    public long count() {
        return leaseRepository.count();
    }

    @Override
    public Notification activate(long id, Long housingId, Model model) {
        Notification notification = new Notification();
        Lease lease = leaseRepository.findById(id).orElse(null);
        if(lease == null) return Notification.error("Contrat de bail introuvable");
        if(lease.getStartDate() != null) return Notification.warn("Ce contrat de bail a déjà été activé");
        try {
            LocalDate now = LocalDate.now();
            lease.setStartDate(now);
            lease.setEndDate(now.plusMonths(lease.getPayment().getMonths()));
            Housing housing;
            if(housingId != null) {
                housing = housingRepository.findById(housingId).orElse(null);
            } else {
                housing = lease.getPayment().getDesiderata();
            }
            if(housing == null || !housing.isActive() || !Availability.FREE.equals(housing.getStatus())){
                model.addAttribute("lease", lease);
                return Notification.error("Le logement sollicité n'est pas disponible");
            }
            if(!Objects.equals(housing.getStanding().getId(), lease.getPayment().getStanding().getId())){
                model.addAttribute("lease", lease);
                return Notification.error("Choisir un logement correspondant au <b>" + lease.getPayment().getStanding().getName() + "<b> standing.");
            }
            lease.setHousing(housing);
            lease = leaseRepository.save(lease);
            Student student = lease.getPayment().getStudent();
            student.setCurrentLease(lease);
            student.setHousing(housing);
            if(housing.getResident() != null) {
                Student resident = housing.getResident();
                resident.setHousing(null);
                studentRepository.save(resident);
            }
            housing.setStatus(Availability.OCCUPIED);
            housing.setResident(student);
            housing.setReservedBy(null);
            studentRepository.save(student);
            housingRepository.save(housing);
            notification.setMessage("Le contract de bail de l'étudiant <b>" + lease.getPayment().getStudent().getUser().getName() + "</b> a été activé avec succès.");
            logRepository.save(Log.info(notification.getMessage()));
        } catch (Throwable e){
            notification = Notification.error("Erreur lors de l'activation du contrat de bail.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return notification;
    }

    @Override
    public Notification mutate(MutationForm mutation, Principal principal) {
        Notification notification = Notification.info();
        Lease lease = leaseRepository.findById(mutation.getLeaseId()).orElse(null);
        if(lease == null) return Notification.warn("Contrat de bail introuvable");
        if(!lease.isMutable()) return Notification.warn("Ce contrat de bail n'est pas modifiable");
        try {
            Housing housing = housingRepository.findById(mutation.getHousingId()).orElse(null);
            if(housing == null) return Notification.error("Logement sollicité introuvable");
            if(!housing.isActive() || !Availability.FREE.equals(housing.getStatus())) return Notification.error("Logement sollicité n'est pas disponible");
            if(Objects.equals(lease.getHousing().getId(), housing.getId())) return Notification.error("Logement sollicité correspond au logement actuel");
            if(Objects.equals(lease.getHousing().getStanding().getId(), housing.getStanding().getId()) && mutation.getAmount() != 0) return Notification.error("Le logement sollicité est du même standing que le logement actuel, donc le montant doit être zéro");
            lease.setMutationHousing(housing);
            lease.setMutationAmount(mutation.getAmount());
            lease.setMutationDate(LocalDateTime.now());
            lease.setMutedBy(userRepository.findByEmail(Optional.ofNullable(principal).map(Principal::getName).orElse("")).orElse(null));
            Student student = lease.getPayment().getStudent();
            housing.setResident(student);
            housing.setReservedBy(null);
            housing.setStatus(Availability.OCCUPIED);
            housingRepository.save(housing);
            student.setHousing(housing);
            studentRepository.save(student);
            notification.setMessage("L'étudiant <b>" + student.getUser().getName() + "</b> a été transféré dans le logement <b>" + housing.getName() + "</b> avec succès.");
            housing = lease.getHousing();
            housing.setResident(null);
            housing.setStatus(Availability.FREE);
            housingRepository.save(housing);
            leaseRepository.save(lease);
            logRepository.save(Log.info(notification.getMessage()));
        } catch (Throwable e){
            notification = Notification.error("Erreur lors de la mutation du contrat de bail.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return notification;
    }
}
