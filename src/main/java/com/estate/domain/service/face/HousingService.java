package com.estate.domain.service.face;

import com.estate.domain.entity.Housing;
import com.estate.domain.entity.Notification;
import com.estate.domain.form.HousingForm;
import com.estate.domain.form.HousingSearch;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface HousingService {

    List<Housing> findAll();

    List<Housing> findAllByStandingId(long standingId);

    List<Housing> findAll(HousingSearch form);

    Optional<Housing> findById(long id);

    RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes);

    long count();

    Notification save(HousingForm form);
}
