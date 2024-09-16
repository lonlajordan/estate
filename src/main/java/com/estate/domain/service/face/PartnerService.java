package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Partner;
import com.estate.domain.form.PartnerForm;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface PartnerService {

    List<Partner> findAll();

    Notification save(PartnerForm form);

    Optional<Partner> findById(long id);

    Notification deleteById(long id, HttpServletRequest request);
}
