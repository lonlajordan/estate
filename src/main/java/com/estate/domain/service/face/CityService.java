package com.estate.domain.service.face;

import com.estate.domain.entity.Housing;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

public interface CityService {
    ModelAndView findAllByProvinceId(long provinceId);

    RedirectView deleteAll(List<Long> ids, RedirectAttributes attributes);

    Optional<Housing> findById(long id);

    ModelAndView getById(long id);

    ModelAndView createOrUpdate(Housing city, long provinceId, Boolean multiple, RedirectAttributes attributes);
}
