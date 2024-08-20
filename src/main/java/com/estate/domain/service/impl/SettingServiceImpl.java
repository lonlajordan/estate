package com.estate.domain.service.impl;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Setting;
import com.estate.domain.service.face.SettingService;
import com.estate.repository.LogRepository;
import com.estate.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
    public RedirectView update(Setting setting, RedirectAttributes attributes) {
        Notification notification = Notification.info();
        Setting setting$ = settingRepository.findById(setting.getId()).orElse(null);
        if(setting$ != null){
            setting$.setValue(setting.getValue());
            try {
                setting$ = settingRepository.save(setting$);
            } catch (Exception e){
                notification = Notification.error("Erreur lors de la modification du paramètre");
                logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
            }
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/setting/list", true);
    }
}
