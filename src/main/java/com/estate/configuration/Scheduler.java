package com.estate.configuration;

import com.estate.domain.entity.Lease;
import com.estate.domain.entity.Student;
import com.estate.domain.entity.Visitor;
import com.estate.domain.enumaration.Gender;
import com.estate.domain.helper.EmailHelper;
import com.estate.domain.service.face.NotificationService;
import com.estate.repository.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    private final EmailHelper emailHelper;
    private final NotificationService notificationService;
    private final LogRepository logRepository;
    private final StudentRepository studentRepository;
    private final HousingRepository housingRepository;
    private final LeaseRepository leaseRepository;
    private final VisitorRepository visitorRepository;

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = new ClassPathResource("one-bills-firebase.json").getInputStream();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            log.error("Error loading Firebase configurations", e);
        }
    }

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
    public void rememberBirthdayAndLeaseExpiration(){
        String to, cc, name, date, messageStudent, messageFirstParent, messageSecondParent, countryCode = "+237";

        List<Student> students = studentRepository.findAllByDateOfBirthAndCurrentLeaseNotNull(LocalDate.now());
        for (Student student : students) {
            try {
                to = student.getUser().getEmail();
                cc = Stream.of(student.getFirstParentEmail(), student.getSecondParentEmail()).distinct().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
                name = student.getUser().getOneName();
                HashMap<String, Object> context = new HashMap<>();
                context.put("name", name);
                emailHelper.sendMail(to, cc, "JOYEUX ANNIVERSAIRE", "birthday.ftl", Locale.FRENCH, context, Collections.emptyList());
                messageStudent = String.format("%1$s %2$s, la MINI CITÉ CONCORDE vous souhaite un joyeux anniversaire ! Que le bonheur et la réussite vous accompagne.", Gender.MALE.equals(student.getUser().getGender()) ? "Cher" : "Chère", name);
                if(StringUtils.startsWith(student.getUser().getPhone(), countryCode)) notificationService.sendSMS(sender, student.getUser().getPhone(), messageStudent);
                if(StringUtils.startsWith(student.getUser().getMobile(), countryCode)) notificationService.sendSMS(sender, student.getUser().getMobile(), messageStudent);
            } catch (Exception e) {
                log.error("Unable to send happy birthday message", e);
            }
        }


        List<Lease> leases = leaseRepository.findAllByEndDateBeforeAndLastRememberDateNull(LocalDate.now().plusDays(30));
        for (Lease lease : leases) {
            if(lease.getNextLease() != null){
                lease.setLastRememberDate(LocalDate.now());
                leaseRepository.save(lease);
                continue;
            }
            try {
                Student student = lease.getPayment().getStudent();
                name = student.getUser().getOneName();
                date = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(lease.getEndDate());
                HashMap<String, Object> context = new HashMap<>();
                context.put("name", name);
                context.put("date", date);
                emailHelper.sendMail(student.getUser().getEmail(), Stream.of(student.getFirstParentEmail(), student.getSecondParentEmail()).distinct().filter(StringUtils::isNotBlank).collect(Collectors.joining(",")), "RENOUVELER LE CONTRAT DE BAIL - CONCORDE", "lease_renewal.ftl", Locale.FRENCH, context, Collections.emptyList());
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
            } catch (Exception e) {
                log.error("Unable to send contract expiration remember message", e);
            }

        }
    }

    @Scheduled(cron = "0 0 10 * * SUN", zone = "GMT+1")
    public void notifyVisitorForHousingAvailability(){
        visitorRepository.deleteAllByCreationDateBefore(LocalDateTime.now().minusYears(1));
        long number = housingRepository.countAllByAvailableTrueAndActiveTrue();
        if(number > 0) {
            List<Visitor> visitors = visitorRepository.findAll();
            for (Visitor visitor : visitors) {
                try {
                    HashMap<String, Object> context = new HashMap<>();
                    context.put("name", visitor.getName());
                    emailHelper.sendMail(visitor.getEmail(), "", "LOGEMENT DISPONIBLE - MINI CITÉ CONCORDE", "housing_available.ftl", Locale.FRENCH, context, Collections.emptyList());
                    // Possibilité d'envoyer aussi un SMS
                } catch (Exception e) {
                    log.error("Unable to send contract expiration remember message", e);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 10 ? * MON", zone = "GMT+1")
    public void sendReminder(){
        sendNotification("ONE BILLS", new Random().nextBoolean() ? "Paye tes factuers ENEO facilement" : "Pay your ENEO bills easily");
    }


    @Scheduled(cron = "0 0 15 ? * SAT", zone = "GMT+1")
    public void sendReminder2(){
        sendNotification("ONE BILLS", new Random().nextBoolean() ? "Collecte tes points bonus, et enjoy avec tes proches" : "Collect your bonus points, and enjoy with your loved ones");
    }



    public void sendNotification(String title, String body) {
        // Create the notification message
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // Create the full FCM message with the target token
        Message message = Message.builder()
                .setTopic("all")
                .setNotification(notification)
                .build();

        // Send the message
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: {}", response);
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }

}
