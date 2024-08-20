package com.estate.domain.service.face;

import com.estate.domain.entity.Standing;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

public interface StandingService {
    Optional<Standing> findById(long id);

    ModelAndView save(Standing pack, boolean multiple, RedirectAttributes attributes);

    List<Standing> findAll();

    RedirectView deleteAllByIdIn(List<Long> ids, RedirectAttributes attributes);
}
