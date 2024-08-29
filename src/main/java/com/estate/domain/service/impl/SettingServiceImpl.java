package com.estate.domain.service.impl;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Setting;
import com.estate.domain.service.face.SettingService;
import com.estate.repository.LogRepository;
import com.estate.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository settingRepository;
    private final LogRepository logRepository;

    @Override
    public Optional<Setting> findById(long id) {
        return settingRepository.findById(id);
    }

    @Override
    public List<Setting> findAll() {
        return settingRepository.findAllByOrderById();
    }

    @Override
    public Notification update(Setting form, Principal principal) {
        Notification notification;
        Setting setting = settingRepository.findById(form.getId()).orElse(null);
        if(setting == null) return Notification.error("Paramètre introuvable");
        setting.setValue(form.getValue());
        try {
            settingRepository.save(setting);
            notification = Notification.info("Le paramètre <b>« " + setting.getCode().getName() + " »</b> a été modifié.");
            logRepository.save(Log.info(notification.getMessage()).author(Optional.ofNullable(principal).map(Principal::getName).orElse("")));
        } catch (Exception e){
            notification = Notification.error("Erreur lors de la modification du paramètre <b>« " + setting.getCode().getName() + " »</b> ");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)).author(Optional.ofNullable(principal).map(Principal::getName).orElse("")));
        }
        return notification;
    }
}
