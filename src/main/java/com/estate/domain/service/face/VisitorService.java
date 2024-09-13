package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Visitor;
import com.estate.domain.form.ContactForm;
import com.estate.domain.form.VisitorForm;

import java.util.List;

public interface VisitorService {
    List<Visitor> findAll();

    Notification save(VisitorForm form);

    Notification contact(ContactForm form);

    Notification subscribe(VisitorForm form);
}
