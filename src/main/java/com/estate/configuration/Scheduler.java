package com.estate.configuration;

import com.estate.domain.entity.Student;
import com.estate.domain.mail.EmailHelper;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
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
    private final StudentRepository studentRepository;

    @Scheduled(cron = "0 0 1 ? * SUN")
    public void generateOrdinaryQuiz(){

    }


    @Scheduled(cron = "0 0 8 * * ?", zone = "GMT+1")
    public void rememberBirthday(){
        List<Student> students = studentRepository.findAllByDateOfBirthAndCurrentLeaseNotNull(LocalDate.now());
        String to, cc, name;
        for (Student student : students) {
            to = student.getEmail();
            cc = Stream.of(student.getFirstParentEmail(), student.getSecondParentEmail()).distinct().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
            name = student.getOneName();
            HashMap<String, Object> context = new HashMap<>();
            context.put("name", name);
            emailHelper.sendMail(to, cc, "JOYEUX ANNIVERSAIRE", "birthday.ftl", Locale.FRENCH, context, Collections.emptyList());
        }
    }
}
