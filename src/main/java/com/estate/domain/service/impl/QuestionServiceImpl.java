package com.estate.domain.service.impl;

import com.estate.configuration.Scheduler;
import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Question;
import com.estate.domain.enumaration.*;
import com.estate.domain.service.face.QuestionService;
import com.estate.repository.LogRepository;
import com.estate.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final LogRepository logRepository;
    private final Scheduler scheduler;

    @Override
    public long countQuestions() {
        return questionRepository.countAllByDeletedFalse();
    }

    @Override
    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Override
    public  Page<Question> questionList(int p) {
        return questionRepository.findAllByOrderByIdDesc(PageRequest.of(p  - 1, 500));
    }

    @Override
    public ModelAndView createOrUpdate(Question question, MultipartFile formulation, MultipartFile justification, Boolean multiple, RedirectAttributes attributes) {
        Question question$ = question;
        boolean creation = true;
        if(question.getId() != null) {
            Optional<Question> _question = questionRepository.findById(question.getId());
            if (_question.isPresent()) {
                question$ = _question.get();
                question$.setAnswer(question.getAnswer());
                question$.setDuration(question.getDuration());
                creation = false;
            }
        }
        Notification notification = new Notification();
        try {
            question$ = questionRepository.save(question$);
            notification.setMessage("Une question a été " + (creation ? "ajoutée." : "modifiée."));
            logRepository.save(Log.info(notification.getMessage()));
            if(creation){
                File folder = new File("questions");
                if(!folder.exists() && !folder.mkdirs()) throw new SecurityException("Erreur lors de la création du dossier de sauvegarde des questions.");
                long time = new Date().getTime();
                String formulationFileName = time + "-" + question$.getId() + "." + FilenameUtils.getExtension(formulation.getOriginalFilename());
                String justificationFileName = time + "-" + question$.getId() + "R." + FilenameUtils.getExtension(justification.getOriginalFilename());
                formulation.transferTo(new File(folder.getAbsolutePath() + File.separator + formulationFileName));
                justification.transferTo(new File(folder.getAbsolutePath() + File.separator + justificationFileName));
                question$.setFormulationFileName(formulationFileName);
                question$.setJustificationFileName(justificationFileName);
                question$.setUploaded(false);
                questionRepository.save(question$);
                scheduler.uploadLocalQuestionVideo(List.of(question$));
            }
            creation = true;
            question$ = new Question();
        } catch (Exception e){
            notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " de la question.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("question", question$);
            view.getModel().put("creation", creation);
            view.getModel().put("notification", notification);
            view.setViewName("admin/question/save");
        }else{
            view.setViewName("redirect:/question/list");
            attributes.addFlashAttribute("notification", notification);
        }
        return view;
    }

    @Override
    public RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        List<Question> questions = questionRepository.findAllByIdIn(new HashSet<>(ids));
        for(Question question: questions){
            try {
                questionRepository.delete(question);
            }catch (Exception e){
                question.setDeleted(true);
                questionRepository.save(question);
                logRepository.save(Log.error("Erreur lors de la suppression de la question <b>" + question.getId() + "</b>.", ExceptionUtils.getStackTrace(e)));
            }
        }
        if(!questions.isEmpty()){
            String plural = questions.size() > 1 ? "s" : "";
            notification = Notification.info( questions.size() + " question" + plural + " supprimée" + plural +".");
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/question/list", true);
    }

    @Override
    public RedirectView saveQuestionsFromSeries(int series, RedirectAttributes attributes) {
        File root = new File("series" + File.separator +  series);
        Notification notification = Notification.info();
        try {
            Map<Integer, String> answers = Files.readAllLines(new File(root.getAbsoluteFile() + File.separator + "correction.txt").toPath())
                    .stream().map(line-> line.split(",")).collect(Collectors.toMap(s -> Integer.valueOf(s[0]), s -> s[1]));
            List<Question> questionList = new ArrayList<>();
            long time = new Date().getTime();
            File destination = new File("questions");
            for(Integer number: answers.keySet()){
                Question question = new Question();
                question.setAnswer(answers.get(number));
                question = questionRepository.save(question);

                File formulation = new File(destination.getAbsoluteFile() + File.separator + time + "-" + question.getId() + ".mp4");
                Files.copy(new File(root.getAbsoluteFile() + File.separator + number + File.separator + (2 * number - 1) + ".mp4").toPath(), formulation.toPath());
                question.setFormulationFileName(formulation.getName());

                File answer = new File(destination.getAbsolutePath() + File.separator + time + "-" + question.getId() + "R.mp4");
                Files.copy(new File(root.getAbsoluteFile() + File.separator + number + File.separator + (2 * number) + ".mp4").toPath(), answer.toPath());
                question.setJustificationFileName(answer.getName());

                question = questionRepository.save(question);
                questionList.add(question);
            }
            scheduler.uploadLocalQuestionVideo(questionList);
        } catch (IOException e) {
            notification = Notification.error("Le bordereau de réponse <b>correction.txt</b> est introuvable.");
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/question/list", true);
    }

    private int getQuestionNumber(String string){
        String str = string.replaceAll("[^\\d]"," ");
        str = str.trim().split("\\s{2,}")[0];
        return Integer.parseInt(str);
    }
}
