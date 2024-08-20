package com.estate.domain.service.face;

import com.estate.domain.entity.Housing;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface HousingService {

    List<Housing> findAll();

    Optional<Housing> findById(long id);

    ModelAndView save(Housing housing, Boolean multiple, RedirectAttributes attributes);

    RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes);

    long count();
}
