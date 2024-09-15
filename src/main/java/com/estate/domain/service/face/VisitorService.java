package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Visitor;
import com.estate.domain.form.ContactForm;
import com.estate.domain.form.VisitorForm;
import com.estate.domain.form.VisitorSearchForm;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VisitorService {
    //Page<Visitor> findAll();

    Page<Visitor> findAll(int page);
    Page<Visitor> findAll(VisitorSearchForm form);

    Notification save(VisitorForm form);

    Notification contact(ContactForm form);

    Notification subscribe(VisitorForm form);
}
