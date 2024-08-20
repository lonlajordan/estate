package com.estate.domain.service.impl;

import com.estate.domain.constant.SettingCode;
import com.estate.domain.entity.Setting;
import com.estate.domain.entity.User;
import com.estate.domain.enumaration.SettingType;
import com.estate.repository.SettingRepository;
import com.estate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InitDataService implements CommandLineRunner {
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;

    @Override
    public void run(String... args) throws Exception {
        User user = userRepository.findByEmail("admin@gmail.com").orElse(new User());
        if(user.getId() == null){
            user = new User();
            user.setEmail("admin@gmail.com");
            user.setFirstName("LONLA");
            user.setLastName("Gatien Jordan");
            user.setPhoneNumber("695463868");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            userRepository.save(user);
        }


        List<Setting> settings = new ArrayList<>(Arrays.asList(
            new Setting(SettingCode.MYSTERY_WINNERS_COUNT, "Nombre de gagnants de la question mystère", SettingType.INTEGER, "10"),
            new Setting(SettingCode.MYSTERY_PARTICIPATION_AMOUNT, "Montant de participation à la question mystère", SettingType.INTEGER, "0"),
            new Setting(SettingCode.ORDINARY_QUIZ_DURATION, "Durée (en minutes) par défaut d'un quiz ordinaire", SettingType.INTEGER, "25"),
            new Setting(SettingCode.ORDINARY_QUIZ_QUESTION_COUNT, "Nombre de questions d'un quiz ordinaire", SettingType.INTEGER, "10"),
            new Setting(SettingCode.ORDINARY_QUIZ_ADVERTISEMENT_COUNT, "Nombre de publicités d'un quiz ordinaire", SettingType.INTEGER, "2"),
            new Setting(SettingCode.ORDINARY_QUIZ_PARTICIPATION_AMOUNT, "Montant de participation au quiz ordinaire", SettingType.INTEGER, "0"),
            new Setting(SettingCode.BATTLE_DURATION, "Durée (en minutes) par défaut d'une battle", SettingType.INTEGER, "45"),
            new Setting(SettingCode.BATTLE_PARTICIPATION_AMOUNT, "Montant de participation à une battle", SettingType.INTEGER, "0"),
            new Setting(SettingCode.DRIVE_TEST_DURATION, "Durée (en minutes) par défaut d'un test conducteur", SettingType.INTEGER, "45"),
            new Setting(SettingCode.DRIVE_TEST_QUESTION_COUNT, "Nombre de questions d'un test conducteur", SettingType.INTEGER, "20"),
            new Setting(SettingCode.DRIVE_TEST_ADVERTISEMENT_COUNT, "Nombre de publicités d'un test conducteur", SettingType.INTEGER, "2"),
            new Setting(SettingCode.DRIVE_TEST_ORGANISATION_AMOUNT, "Frais d'organisation d'un test conducteur", SettingType.INTEGER, "0"),
            new Setting(SettingCode.BATTLE_QUESTION_COUNT, "Nombre de questions d'une battle", SettingType.INTEGER, "20"),
            new Setting(SettingCode.BATTLE_ADVERTISEMENT_COUNT, "Nombre de publicités d'une battle", SettingType.INTEGER, "2")
        ));
        settingRepository.saveAll(settings.stream().filter(setting -> !settingRepository.existsByCode(setting.getCode())).collect(Collectors.toList()));
        List<String> codes = settings.stream().map(Setting::getCode).collect(Collectors.toList());
        settings = settingRepository.findAll().stream().filter(setting -> !codes.contains(setting.getCode())).collect(Collectors.toList());
        settingRepository.deleteAll(settings);
    }
}
