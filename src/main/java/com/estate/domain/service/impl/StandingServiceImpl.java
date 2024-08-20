package com.estate.domain.service.impl;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Standing;
import com.estate.domain.enumaration.Level;
import com.estate.domain.service.face.StandingService;
import com.estate.repository.LogRepository;
import com.estate.repository.StandingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StandingServiceImpl implements StandingService {
    private final StandingRepository standingRepository;
    private final LogRepository logRepository;

    @Override
    public List<Standing> findAll() {
        return standingRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Optional<Standing> findById(long id) {
        return standingRepository.findById(id);
    }

    @Override
    public ModelAndView save(Standing standing, boolean multiple, RedirectAttributes attributes) {
        Standing standing$ = standing;
        boolean creation = true;
        if(standing.getId() != null){
            Optional<Standing> _standing = standingRepository.findById(standing.getId());
            if(_standing.isPresent()){
                standing$ = _standing.get();
                standing$.setName(standing.getName());
                standing$.setRent(standing.getRent());
                standing$.setCaution(standing.getCaution());
                standing$.setRepair(standing.getRepair());
                creation = false;
            }
        }
        Notification notification;
        try {
            standingRepository.save(standing$);
            notification = Notification.info("Le <b>" + standing$.getName() +"</b> standing a été " + (creation ? "ajouté." : "modifié."));
            creation = true;
            standing$ = new Standing();
        } catch (Exception e){
            notification = Notification.error("Erreur lors de la " + (creation ? "création" : "modification") + " du <b>" + standing$.getName() + "</b> standing.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("standing", standing$);
            view.getModel().put("creation", creation);
            view.getModel().put("notification", notification);
            view.setViewName("admin/standing/save");
        }else{
            attributes.addFlashAttribute("notification", notification);
            view.setViewName("redirect:/standing/list");
        }
        return view;
    }

    @Override
    public RedirectView deleteAllByIdIn(List<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            standingRepository.deleteAllById(ids);
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des standings.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/standing/list", true);
    }
}
