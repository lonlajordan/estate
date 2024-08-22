package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Standing;
import com.estate.domain.form.StandingForm;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

public interface StandingService {
    Optional<Standing> findById(long id);

    List<Standing> findAll();

    RedirectView deleteById(long id, RedirectAttributes attributes);

    Notification save(StandingForm form);
}
