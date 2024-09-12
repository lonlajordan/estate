package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Visitor;
import com.estate.domain.form.VisitorForm;

import java.util.List;
import java.util.Optional;

public interface VisitorService {
    List<Visitor> findAll();

    Optional<Visitor> findByEmail(String email);

    Notification save(VisitorForm visitorForm);

    void submitVisitor(VisitorForm visitor);
}
