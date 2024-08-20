package com.estate.domain.service.face;

import com.estate.domain.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

public interface MediaService {

    Page<Media> mediaList(int p);

    Optional<Media> findById(long id);

    ModelAndView save(Media media, MultipartFile file, Boolean multiple, RedirectAttributes attributes);

    RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes);
}
