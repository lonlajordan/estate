package com.estate.domain.service.face;

import com.estate.domain.entity.Setting;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

public interface SettingService {
    List<Setting> findAll();

    Optional<Setting> findById(long id);

    RedirectView update(Setting setting, RedirectAttributes attributes);
}
