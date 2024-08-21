package com.estate.domain.service.impl;

import com.estate.domain.entity.Housing;
import com.estate.domain.service.face.HousingService;
import com.estate.repository.HousingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HousingServiceImpl implements HousingService {
    private final HousingRepository housingRepository;

    @Override
    public List<Housing> findAll() {
        return housingRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Optional<Housing> findById(long id) {
        return Optional.empty();
    }

    @Override
    public ModelAndView save(Housing housing, Boolean multiple, RedirectAttributes attributes) {
        return null;
    }

    @Override
    public RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes) {
        return null;
    }

    @Override
    public long count() {
        return housingRepository.count();
    }
}
