package com.estate.domain.service.face;

import com.estate.domain.entity.Mystery;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

public interface MysteryService {
    Page<Mystery> mysteryList(int p);

    ModelAndView details(long id);

    Optional<Mystery> findById(long id);

    ModelAndView createOrUpdate(Mystery mystery, Boolean multiple, RedirectAttributes attributes);

    RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes);

    long countMysteries();
}
