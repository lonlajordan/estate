package com.estate.domain.service.face;

import com.estate.domain.entity.Housing;
import com.estate.domain.entity.Notification;
import com.estate.domain.enumaration.Availability;
import com.estate.domain.form.HousingForm;
import com.estate.domain.form.HousingSearch;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface HousingService {

    List<Housing> findAll();

    List<Housing> findAllByStandingIdAndStatusAndActiveTrue(long standingId, Availability status);

    List<Housing> findAllByStandingIdAndActiveTrue(long standingId);

    List<Housing> findAllByStatusAndActiveTrue(Availability status);

    List<Housing> findAll(HousingSearch form);

    Optional<Housing> findById(long id);

    Notification deleteById(long id, boolean force, HttpServletRequest request);

    long count();

    Notification save(HousingForm form, Principal principal);

    Notification toggleById(long id, Principal principal);

    Notification liberate(long id, Principal principal);
}
