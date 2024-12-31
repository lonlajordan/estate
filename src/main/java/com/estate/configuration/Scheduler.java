package com.estate.configuration;

import com.estate.domain.entity.Lease;
import com.estate.domain.entity.Student;
import com.estate.domain.enumaration.Gender;
import com.estate.domain.helper.EmailHelper;
import com.estate.domain.service.face.NotificationService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {
    private final EmailHelper emailHelper;
    private final NotificationService notificationService;
    private final LogRepository logRepository;
    private final StudentRepository studentRepository;
    private final LeaseRepository leaseRepository;
    private final VisitorRepository visitorRepository;

    @Value("${sms.sender}")
    private String sender;

    @Scheduled(cron = "@daily", zone = "GMT+1")
    public void updateStudentCurrentLease(){
        List<Student> students = studentRepository.findAllByHavingPendingLease(LocalDate.now());
        for (Student student : students) {
            if(student.getCurrentLease() != null && student.getCurrentLease().getNextLease() != null) {
                student.setCurrentLease(student.getCurrentLease().getNextLease());
                studentRepository.save(student);
            }
        }
    }

    @Scheduled(cron = "@monthly", zone = "GMT+1")
    private void deleteLogs(){
        logRepository.deleteAllByCreationDateBefore(LocalDateTime.now().minusDays(90));
    }

    @Scheduled(cron = "0 0 8 * * ?", zone = "GMT+1")
    public void rememberBirthday(){
        String to, cc, name, date, messageStudent, messageFirstParent, messageSecondParent, countryCode = "+237";

        List<Student> students = studentRepository.findAllByDateOfBirthAndCurrentLeaseNotNull(LocalDate.now());
        for (Student student : students) {
            to = student.getUser().getEmail();
            cc = Stream.of(student.getFirstParentEmail(), student.getSecondParentEmail()).distinct().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
            name = student.getUser().getOneName();
            HashMap<String, Object> context = new HashMap<>();
            context.put("name", name);
            emailHelper.sendMail(to, cc, "JOYEUX ANNIVERSAIRE", "birthday.ftl", Locale.FRENCH, context, Collections.emptyList());
            messageStudent = String.format("%1$s %2$s, la MINI CITÉ CONCORDE vous souhaite un joyeux anniversaire ! Que le bonheur et la réussite vous accompagne.", Gender.MALE.equals(student.getUser().getGender()) ? "Cher" : "Chère", name);
            if(StringUtils.startsWith(student.getUser().getPhone(), countryCode)) notificationService.sendSMS(sender, student.getUser().getPhone(), messageStudent);
            if(StringUtils.startsWith(student.getUser().getMobile(), countryCode)) notificationService.sendSMS(sender, student.getUser().getMobile(), messageStudent);
        }


        List<Lease> leases = leaseRepository.findAllByEndDateBeforeAndLastRememberDateNull(LocalDate.now().plusDays(30));
        for (Lease lease : leases) {
            if(lease.getNextLease() != null){
                lease.setLastRememberDate(LocalDate.now());
                leaseRepository.save(lease);
                continue;
            }
            Student student = lease.getPayment().getStudent();
            name = student.getUser().getOneName();
            date = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(lease.getEndDate());
            HashMap<String, Object> context = new HashMap<>();
            context.put("name", name);
            context.put("date", date);
            emailHelper.sendMail(Stream.of(student.getFirstParentEmail(), student.getSecondParentEmail()).distinct().filter(StringUtils::isNotBlank).collect(Collectors.joining(",")), student.getUser().getEmail(), "RAPPEL :: RENOUVELLEMENT DE CONTRAT DE BAIL - CONCORDE", "lease_renewal.ftl", Locale.FRENCH, context, Collections.emptyList());
            lease.setLastRememberDate(LocalDate.now());
            messageStudent = String.format("%1$s %2$s, votre contrat de bail arrive à échéance le %3$s. Nous vous proposons de le renouveler. Merci.", Gender.MALE.equals(student.getUser().getGender()) ? "Cher" : "Chère", name, date);
            messageFirstParent = String.format("Cher parent, le contrat de bail de votre %1$s %2$s arrive à échéance le %3$s. Nous vous proposons de le renouveler. Merci.", Gender.MALE.equals(student.getUser().getGender()) ? student.getFirstParentRelation().getBoy() : student.getFirstParentRelation().getGirl() ,  name, date);
            messageSecondParent = String.format("Cher parent, le contrat de bail de votre %1$s %2$s arrive à échéance le %3$s. Nous vous proposons de le renouveler. Merci.", Gender.MALE.equals(student.getUser().getGender()) ? student.getSecondParentRelation().getBoy() : student.getSecondParentRelation().getGirl() ,  name, date);
            leaseRepository.save(lease);
            if(StringUtils.startsWith(student.getUser().getPhone(), countryCode)) notificationService.sendSMS(sender, student.getUser().getPhone(), messageStudent);
            if(StringUtils.startsWith(student.getFirstParentPhone(), countryCode)) notificationService.sendSMS(sender, student.getFirstParentPhone(), messageFirstParent);
            if(StringUtils.startsWith(student.getSecondParentPhone(), countryCode)) notificationService.sendSMS(sender, student.getSecondParentPhone(), messageSecondParent);
            if(StringUtils.startsWith(student.getUser().getMobile(), countryCode)) notificationService.sendSMS(sender, student.getUser().getMobile(), messageStudent);
            if(StringUtils.startsWith(student.getFirstParentMobile(), countryCode)) notificationService.sendSMS(sender, student.getFirstParentMobile(), messageFirstParent);
            if(StringUtils.startsWith(student.getSecondParentMobile(), countryCode)) notificationService.sendSMS(sender, student.getSecondParentMobile(), messageSecondParent);
        }
    }

    @Scheduled(cron = "0 0 4 * * ?", zone = "GMT+1")
    public void deleteVisitors(){
        visitorRepository.deleteAllByCreationDateBefore(LocalDateTime.now().minusYears(1));
    }

}
