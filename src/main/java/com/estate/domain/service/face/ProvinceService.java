package com.estate.domain.service.face;

import com.estate.domain.entity.Housing;
import com.estate.domain.entity.Province;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ProvinceService {
    List<Province> findAll();

    List<Housing> findAllCityByProvinceId(long provinceId);

    Optional<Province> findById(long id);

    ModelAndView createOrUpdate(Province province, Boolean multiple, RedirectAttributes attributes);

    RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes);
}
