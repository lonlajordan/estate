package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Payment;
import com.estate.domain.enumaration.Status;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Page<Payment> findAll(int p);

    Optional<Payment> findById(long id);

    ModelAndView search(String name, String phone, Status status, Date start, Date end, int page);

    ModelAndView statistics();

    RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes);

    Notification status(long id);
}
