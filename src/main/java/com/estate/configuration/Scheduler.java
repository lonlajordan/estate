package com.estate.configuration;

import com.estate.domain.entity.Lease;
import com.estate.domain.entity.Student;
import com.estate.domain.mail.EmailHelper;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {
    private final EmailHelper emailHelper;
    private final LogRepository logRepository;
    private final StudentRepository studentRepository;
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

    @Scheduled(cron = "0 0 4 * * ?", zone = "GMT+1")
    public void deleteVisitors(){
        visitorRepository.deleteAllByCreationDateBefore(LocalDateTime.now().minusYears(1));
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
