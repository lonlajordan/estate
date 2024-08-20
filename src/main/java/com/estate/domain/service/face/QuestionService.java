package com.estate.domain.service.face;

import com.estate.domain.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Optional;

public interface QuestionService {

    Page<Question> questionList(int p);

    Optional<Question> findById(long id);

    ModelAndView createOrUpdate(Question question, MultipartFile formulation, MultipartFile justification, Boolean multiple, RedirectAttributes attributes);

    RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes);

    RedirectView saveQuestionsFromSeries(int series, RedirectAttributes attributes);

    long countQuestions();
}
