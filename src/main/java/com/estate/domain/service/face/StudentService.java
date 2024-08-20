package com.estate.domain.service.face;

import com.estate.domain.entity.Student;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Optional<Student> findById(long id);
    void deleteById(long id);

    ModelAndView playerList(int p);

    RedirectView renameById(long id, RedirectAttributes attributes);

    RedirectView toggleById(long id, RedirectAttributes attributes);

    RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes);

    ModelAndView search(int page, String firstName, String lastName, String pseudo, Integer birthYear, Long provinceId, Long cityId, String phoneNumber, String email, Character sex, int status);

    ModelAndView playersDetails();

    long countPlayers();
}
