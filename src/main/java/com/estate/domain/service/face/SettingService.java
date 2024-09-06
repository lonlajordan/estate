package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Setting;
import com.estate.domain.form.SettingForm;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface SettingService {
    List<Setting> findAll();

    Optional<Setting> findById(long id);

    Notification update(SettingForm form, Principal principal);

    Notification savePolicy(MultipartFile file);
}
