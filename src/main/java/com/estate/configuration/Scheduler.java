package com.estate.configuration;

import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.transaction.Transactional;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {
    private final StudentRepository studentRepository;

    @Scheduled(cron = "0 0 1 ? * SUN")
    public void generateOrdinaryQuiz(){

    }


    @Scheduled(cron = "0 0/15 * * * ?")
    @Transactional
    public void closeGames(){

    }

    /*
    @Async
    public void uploadLocalQuestionVideo(List<Question> questions){
        if(questions.isEmpty()) questions = questionRepository.findAllByUploadedFalse();
        File folder = new File("questions");

        for(Question question: questions){
            String formulationFileName = "questions/" + question.getFormulationFileName();
            String justificationFileName = "questions/" + question.getJustificationFileName();
            File formulation = new File(folder.getAbsolutePath() + File.separator + question.getFormulationFileName());
            File justification = new File(folder.getAbsolutePath() + File.separator + question.getJustificationFileName());

            try {
                if(formulation.exists()){
                    question.setFormulationSize(formulation.length());
                    question.setUploaded(true);
                    FileUtils.deleteQuietly(formulation);
                }
            }catch (Exception ignored) {}
            try {
                if(justification.exists()){
                    question.setJustificationSize(justification.length());
                    FileUtils.deleteQuietly(justification);
                }
            }catch (Exception ignored) {}
            question.setFormulationFileName(formulationFileName);
            question.setJustificationFileName(justificationFileName);
            questionRepository.save(question);
        }
    }*/
}
