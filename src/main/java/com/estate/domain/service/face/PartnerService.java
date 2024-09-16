package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Partner;
import com.estate.domain.form.PartnerForm;

import java.util.List;

public interface PartnerService {

    List<Partner> findAll();

    Notification save(PartnerForm partnerForm);
}
