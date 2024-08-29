package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Setting;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface SettingService {
    List<Setting> findAll();

    Optional<Setting> findById(long id);

    Notification update(Setting form, Principal principal);
}
