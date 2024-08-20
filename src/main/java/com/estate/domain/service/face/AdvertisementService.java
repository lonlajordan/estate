package com.estate.domain.service.face;

import com.estate.domain.entity.Advertisement;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

public interface AdvertisementService {
    List<Advertisement> findRandomly(int n);

    List<Advertisement> findAll();

    Optional<Advertisement> findById(long id);
    
    ModelAndView save(Advertisement advertisement, MultipartFile file, Boolean multiple, RedirectAttributes attributes);

    RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes);

    long countAdvertisements();
}
