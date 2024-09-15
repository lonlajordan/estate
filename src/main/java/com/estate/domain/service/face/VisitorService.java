package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Student;
import com.estate.domain.entity.Visitor;
import com.estate.domain.form.StudentSearch;
import com.estate.domain.form.VisitorForm;
import com.estate.domain.form.VisitorSearchForm;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface VisitorService {
    //Page<Visitor> findAll();

    Page<Visitor> findAll(int page);
    Page<Visitor> findAll(VisitorSearchForm form);

    Optional<Visitor> findByEmail(String email);

    Notification save(VisitorForm visitorForm);

    void submitVisitor(VisitorForm visitor);
}
