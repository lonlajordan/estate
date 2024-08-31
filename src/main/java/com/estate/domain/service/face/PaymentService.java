package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Payment;
import com.estate.domain.enumaration.Status;
import com.estate.domain.form.PaymentForm;
import com.estate.domain.form.PaymentSearch;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Page<Payment> findAll(int page);

    Page<Payment> findAll(PaymentSearch form);

    Optional<Payment> findById(long id);

    long countByStatus(Status status);

    Notification save(PaymentForm form, Principal principal);

    List<Payment> findAllByStatusAndYear(Status status, Integer year);

    Notification validate(long id, HttpSession session);

    Notification submit(long id);
}
