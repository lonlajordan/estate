package com.estate.domain.service.impl;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Student;
import com.estate.domain.enumaration.Level;
import com.estate.domain.form.StudentForm;
import com.estate.domain.form.StudentSearch;
import com.estate.domain.service.face.StudentService;
import com.estate.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    
    @Override
    public Page<Student> findAll(int page) {
        return studentRepository.findAllByOrderByCreationDateDesc(PageRequest.of(page - 1, 100));
    }

    @Override
    public Page<Student> findAll(StudentSearch form) {
        return studentRepository.findAll(form.toSpecification(), PageRequest.of(form.getPage() == null ? 0 : (form.getPage() - 1), 100));
    }

    @Override
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    @Transactional
    public Notification save(StudentForm form) {
        boolean creation = form.getId() == null;
        Notification notification = Notification.info();
        Student student = creation ? new Student() : studentRepository.findById(form.getId()).orElse(null);
        if(student == null) return Notification.error("Visiteur introuvable");
        student.setFirstName(form.getFirstName());
        student.setLastName(form.getLastName());
        student.setDateOfBirth(form.getDateOfBirth());
        student.setPlaceOfBirth(form.getPlaceOfBirth());
        student.setGender(form.getGender());

        student.setSchool(form.getSchool());
        student.setSpecialities(form.getSpecialities());
        student.setGrade(form.getGrade());
        student.setPhone(form.getPhone());
        student.setEmail(form.getEmail());

        student.setFirstParentRelation(form.getFirstParentRelation());
        student.setFirstParentName(form.getFirstParentName());
        student.setFirstParentAddress(form.getFirstParentAddress());
        student.setFirstParentPhone(form.getFirstParentPhone());
        student.setFirstParentEmail(form.getFirstParentEmail());

        student.setSecondParentRelation(form.getSecondParentRelation());
        student.setSecondParentName(form.getSecondParentName());
        student.setSecondParentAddress(form.getSecondParentAddress());
        student.setSecondParentPhone(form.getSecondParentPhone());
        student.setSecondParentEmail(form.getSecondParentEmail());

        try {
            if(creation){
                // generate password and send mail
                // Send mail here
            }
            studentRepository.saveAndFlush(student);
            notification.setMessage("Un étudiant a été " + (creation ? "ajouté." : "modifié."));
            log.info(notification.getMessage());
        } catch (Throwable e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String message = ExceptionUtils.getRootCauseMessage(e);
            notification.setType(Level.ERROR);
            if(StringUtils.containsIgnoreCase(message, "UK_EMAIL")){
                notification.setMessage("Adresse e-mail existante");
            }else if(StringUtils.containsIgnoreCase(message, "UK_PHONE")){
                notification.setMessage("Numéro de téléphone existant");
            } else {
                notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " de l'étudiant.");
            }
            log.error(notification.getMessage(), e);
        }

        return notification;
    }
}
