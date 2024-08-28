package com.estate.domain.service.impl;

import com.estate.domain.entity.Log;
import com.estate.domain.entity.Notification;
import com.estate.domain.entity.User;
import com.estate.domain.form.LogSearch;
import com.estate.domain.service.face.LogService;
import com.estate.repository.LogRepository;
import com.estate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Log> findAll(int page){
        return logRepository.findAllByOrderByCreationDateDesc(PageRequest.of(page  - 1, 500));
    }

    @Override
    public Page<Log> findAll(LogSearch form){
        return logRepository.findAll(form.toSpecification(), PageRequest.of(form.getPage()  - 1, 500, Sort.by(Sort.Direction.DESC, "creationDate")));
    }

    @Override
    public Optional<Log> findById(long id){
        return logRepository.findById(id);
    }

    @Override
    public RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            int n = ids.size();
            logRepository.deleteAllById(ids);
            String plural = n > 1 ? "s" : "";
            notification.setMessage( n + " évènement" + plural + " supprimé" + plural +".");
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des évènements.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/log/list", true);
    }

    @Override
    public void handleError(Integer status, HttpSession session, Model model, Principal principal, Exception exception) {
        getConnectedUser(session, principal);
        String title = "Erreur";
        String details = "Une erreur s'est produite lors de cette opération. Veuillez contacter votre administrateur.";
        switch (status) {
            case 401:
            case 403:
                title = "Accès refusé";
                details = "Vous n'avez pas les droits pour accéder à cette page. Veuillez contacter votre administrateur.";
                break;
            case 404:
                title = "Page Introuvable";
                details = "La page ou la ressource sollicitée est introuvable.";
                break;
            case 500:
                title = "Erreur Serveur";
                details = "Une erreur s'est produite sur le serveur.";
                logRepository.save(Log.error(details, ExceptionUtils.getStackTrace(exception)));
                break;
            default:
                break;
        }
        model.addAttribute("title", title);
        model.addAttribute("details", details);
    }

    public void getConnectedUser(HttpSession session, Principal principal){
        User user = (User) session.getAttribute("user");
        if(user == null && userRepository != null && principal != null){
            user = userRepository.findByEmail(principal.getName()).orElse(null);
            session.setAttribute("user", user);
        }
    }
}
