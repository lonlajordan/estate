package com.estate.configuration;

import com.estate.domain.entity.Lease;
import com.estate.domain.entity.Student;
import com.estate.domain.mail.EmailHelper;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final LogRepository logRepository;
    private final StudentRepository studentRepository;
    private final LeaseRepository leaseRepository;

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
        List<Student> students = studentRepository.findAllByDateOfBirthAndCurrentLeaseNotNull(LocalDate.now());
        String to, cc, name;
        for (Student student : students) {
            to = student.getUser().getEmail();
            cc = Stream.of(student.getFirstParentEmail(), student.getSecondParentEmail()).distinct().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
            name = student.getUser().getOneName();
            HashMap<String, Object> context = new HashMap<>();
            context.put("name", name);
            emailHelper.sendMail(to, cc, "JOYEUX ANNIVERSAIRE", "birthday.ftl", Locale.FRENCH, context, Collections.emptyList());
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
            HashMap<String, Object> context = new HashMap<>();
            context.put("name", name);
            emailHelper.sendMail(Stream.of(student.getFirstParentEmail(), student.getSecondParentEmail()).distinct().filter(StringUtils::isNotBlank).collect(Collectors.joining(",")), student.getUser().getEmail(), "RAPPEL DE PAIEMENT DU LOYER", "birthday.ftl", Locale.FRENCH, context, Collections.emptyList());
            lease.setLastRememberDate(LocalDate.now());
            leaseRepository.save(lease);
        }
    }
}
