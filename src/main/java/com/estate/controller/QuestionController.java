package com.estate.controller;

import com.estate.domain.entity.Question;
import com.estate.domain.service.face.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Question> questions = questionService.questionList(p);
        model.addAttribute("questions", questions.toList());
        model.addAttribute("totalPages", questions.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/question/list";
    }

    @GetMapping(value="view/{id}")
    public String view(@PathVariable long id, Model model){
        model.addAttribute("question", questionService.findById(id));
        return "admin/question/view";
    }

    @GetMapping(value = "save")
    private String getQuestion(@RequestParam(required = false, defaultValue = "-1") long id, Model model){
        Question question = questionService.findById(id).orElse(new Question());
        model.addAttribute("question", question);
        model.addAttribute("creation", question.getId() == null);
        return "admin/question/save";
    }

    @PostMapping(value="save")
    public ModelAndView save(@Valid Question question, @RequestParam(required = false) MultipartFile formulation, @RequestParam(required = false) MultipartFile justification, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes){
        return questionService.createOrUpdate(question, formulation, justification, multiple, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam ArrayList<Long> ids, RedirectAttributes attributes){
        return questionService.deleteAllByIds(ids, attributes);
    }

    @GetMapping(value="import")
    public String importation(){
        return "admin/question/import";
    }


    @GetMapping(value="series")
    public String series(){
        return "admin/question/series";
    }

    @PostMapping(value="series")
    public RedirectView saveSeries(@RequestParam int series, RedirectAttributes attributes) {
        return questionService.saveQuestionsFromSeries(series, attributes);
    }
}
