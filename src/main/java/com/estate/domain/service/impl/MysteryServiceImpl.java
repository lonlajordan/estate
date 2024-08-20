package com.estate.domain.service.impl;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.*;
import com.estate.domain.enumaration.Level;
import com.estate.domain.enumaration.QuestionType;
import com.estate.domain.service.face.MysteryService;
import com.estate.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MysteryServiceImpl implements MysteryService {
    private final MysteryRepository mysteryRepository;
    private final LogRepository logRepository;
    private final ProvinceRepository provinceRepository;

    @Override
    public long countMysteries() {
        return mysteryRepository.countAllByDeletedFalse();
    }

    @Override
    public Page<Mystery> mysteryList(int p) {
        return mysteryRepository.findAllByDeletedFalseOrderByWeekDesc(PageRequest.of(p  - 1, 500));
    }

    @Override
    public ModelAndView createOrUpdate(Mystery mystery, Boolean multiple, RedirectAttributes attributes) {
        Mystery mystery$ = mystery;
        boolean creation = true;
        if(mystery.getId() != null) {
            Optional<Mystery> _mystery = mysteryRepository.findById(mystery.getId());
            if (_mystery.isPresent()) {
                mystery$ = _mystery.get();
                mystery$.setWeek(mystery.getWeek());
                mystery$.setFrAnswer(mystery.getFrAnswer());
                mystery$.setEnFormulation(mystery.getEnFormulation());
                mystery$.setEsFormulation(mystery.getEsFormulation());
                mystery$.setArFormulation(mystery.getArFormulation());
                mystery$.setPtFormulation(mystery.getPtFormulation());
                if(QuestionType.QCM.equals(mystery$.getType())){
                    mystery$.setEnPropositionA(mystery.getEnPropositionA());
                    mystery$.setEnPropositionB(mystery.getEnPropositionB());
                    mystery$.setEnPropositionC(mystery.getEnPropositionC());
                    mystery$.setEnPropositionD(mystery.getEnPropositionD());

                    mystery$.setEsPropositionA(mystery.getEsPropositionA());
                    mystery$.setEsPropositionB(mystery.getEsPropositionB());
                    mystery$.setEsPropositionC(mystery.getEsPropositionC());
                    mystery$.setEsPropositionD(mystery.getEsPropositionD());

                    mystery$.setArPropositionA(mystery.getArPropositionA());
                    mystery$.setArPropositionB(mystery.getArPropositionB());
                    mystery$.setArPropositionC(mystery.getArPropositionC());
                    mystery$.setArPropositionD(mystery.getArPropositionD());

                    mystery$.setPtPropositionA(mystery.getPtPropositionA());
                    mystery$.setPtPropositionB(mystery.getPtPropositionB());
                    mystery$.setPtPropositionC(mystery.getPtPropositionC());
                    mystery$.setPtPropositionD(mystery.getPtPropositionD());
                }else{
                    mystery$.setEnAnswer(mystery.getEnAnswer());
                    mystery$.setEsAnswer(mystery.getEsAnswer());
                    mystery$.setArAnswer(mystery.getArAnswer());
                    mystery$.setPtAnswer(mystery.getPtAnswer());
                }
                creation = false;
            }
        }
        mystery$.setFrFormulation(mystery.getFrFormulation());
        mystery$.setFrAnswer(mystery.getFrAnswer());
        if(creation){
            mystery$.setEnFormulation(mystery.getFrFormulation());
            mystery$.setEsFormulation(mystery.getFrFormulation());
            mystery$.setArFormulation(mystery.getFrFormulation());
            mystery$.setPtFormulation(mystery.getFrFormulation());
        }
        if(QuestionType.QCM.equals(mystery$.getType())){
            mystery$.setFrPropositionA(mystery.getFrPropositionA());
            mystery$.setFrPropositionB(mystery.getFrPropositionB());
            mystery$.setFrPropositionC(mystery.getFrPropositionC());
            mystery$.setFrPropositionD(mystery.getFrPropositionD());
            if(creation){
                mystery$.setEnPropositionA(mystery.getFrPropositionA());
                mystery$.setEnPropositionB(mystery.getFrPropositionB());
                mystery$.setEnPropositionC(mystery.getFrPropositionC());
                mystery$.setEnPropositionD(mystery.getFrPropositionD());

                mystery$.setEsPropositionA(mystery.getFrPropositionA());
                mystery$.setEsPropositionB(mystery.getFrPropositionB());
                mystery$.setEsPropositionC(mystery.getFrPropositionC());
                mystery$.setEsPropositionD(mystery.getFrPropositionD());

                mystery$.setArPropositionA(mystery.getFrPropositionA());
                mystery$.setArPropositionB(mystery.getFrPropositionB());
                mystery$.setArPropositionC(mystery.getFrPropositionC());
                mystery$.setArPropositionD(mystery.getFrPropositionD());

                mystery$.setPtPropositionA(mystery.getFrPropositionA());
                mystery$.setPtPropositionB(mystery.getFrPropositionB());
                mystery$.setPtPropositionC(mystery.getFrPropositionC());
                mystery$.setPtPropositionD(mystery.getFrPropositionD());
            }
        }else{
            if(creation){
                mystery$.setEnAnswer(mystery.getFrAnswer());
                mystery$.setEsAnswer(mystery.getFrAnswer());
                mystery$.setArAnswer(mystery.getFrAnswer());
                mystery$.setPtAnswer(mystery.getFrAnswer());
            }
        }
        Notification notification = new Notification();
        try {
            mystery$ = mysteryRepository.save(mystery$);
            notification.setMessage("Une question mystère a été " + (creation ? "ajoutée." : "modifiée."));
            logRepository.save(Log.info(notification.getMessage()));
            creation = true;
            mystery$ = new Mystery();
        } catch (Exception e){
            notification.setType(Level.ERROR);
            notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " de la question.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("mystery", mystery$);
            view.getModel().put("creation", creation);
            view.getModel().put("notification", notification);
            view.setViewName("admin/mystery/save");
        }else{
            view.setViewName("redirect:/mystery/list");
            attributes.addFlashAttribute("notification", notification);
        }
        return view;
    }

    @Override
    public Optional<Mystery> findById(long id) {
        return mysteryRepository.findById(id);
    }

    @Override
    public RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes){
        int n = 0;
        for(Long id: ids){
            Mystery mystery = mysteryRepository.findById(id).orElse(null);
            if(mystery != null){
                try {
                    mysteryRepository.delete(mystery);
                }catch (Exception e){
                    mystery.setDeleted(true);
                    mysteryRepository.save(mystery);
                    logRepository.save(Log.error("Erreur lors de la suppression de la question <b>" + id + "</b>.", ExceptionUtils.getStackTrace(e)));
                }
                n++;
            }
        }
        String plural = n > 1 ? "s" : "";
        Notification notification =Notification.info( n + " question" + plural + " supprimée" + plural +".");
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/mystery/list", true);
    }

    @Override
    public ModelAndView details(long id) {
        ModelAndView view = new ModelAndView("admin/mystery/view");
        Mystery mystery = mysteryRepository.findById(id).orElse(null);
        if(mystery != null){
            List<Province> provinces = provinceRepository.findAll();
            List<List<long[]>> participations = new ArrayList<>();
            List<long[]> totals = new ArrayList<>();

            view.getModel().put("mystery", mystery);
            view.getModel().put("provinces", provinces);
            view.getModel().put("totals", totals);
            view.getModel().put("participations", participations);
        }else{
            view.setViewName("redirect:/error/404");
        }
        return view;
    }
}
