package com.estate.domain.service.face;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Merchant;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface MerchantService {
    ModelAndView createOrUpdate(Merchant merchant, Boolean multiple, RedirectAttributes attributes);

    RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes);

    List<Merchant> findAll();
    Notification toggleById(long id);

    Optional<Merchant> findById(long id);
}
