package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Payment;
import com.estate.domain.enumaration.Status;
import com.estate.domain.form.PaymentForm;
import com.estate.domain.form.PaymentSearch;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

public interface PaymentService {

    Page<Payment> findAll(int page);

    Page<Payment> findAll(PaymentSearch form);

    Optional<Payment> findById(long id);

    RedirectView deleteById(long id, RedirectAttributes attributes);

    Notification status(long id);

    long countByStatus(Status status);

    Notification save(PaymentForm form);
}
