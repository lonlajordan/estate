package com.estate.domain.service.face;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Recharge;
import com.estate.domain.enumaration.Operator;
import com.estate.domain.enumaration.Status;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RechargeService {

    Page<Recharge> findAll(int p);

    Optional<Recharge> findById(long id);

    ModelAndView search(String name, String phone, Operator operator, Status status, Date start, Date end, int page);

    ModelAndView statistics();

    RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes);

    Notification status(long id);
}
