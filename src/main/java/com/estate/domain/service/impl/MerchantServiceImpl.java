package com.estate.domain.service.impl;

import com.estate.domain.enumaration.Level;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Merchant;
import com.estate.domain.dto.Notification;
import com.estate.domain.service.face.MerchantService;
import com.estate.repository.LogRepository;
import com.estate.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    private final LogRepository logRepository;
    private final Logger logger = LoggerFactory.getLogger(MerchantServiceImpl.class);

    @Override
    public Optional<Merchant> findById(long id){
        return merchantRepository.findById(id);
    }

    @Override
    public List<Merchant> findAll(){
        return merchantRepository.findAll();
    }

    @Override
    public ModelAndView createOrUpdate(Merchant merchant, Boolean multiple, RedirectAttributes attributes){
        Merchant merchant$ = merchant;
        boolean creation = true;
        if(merchant.getId() != null){
            Optional<Merchant> _merchant = merchantRepository.findById(merchant.getId());
            if(_merchant.isPresent()){
                merchant$ = _merchant.get();
                merchant$.setCode(merchant.getCode());
                merchant$.setOperator(merchant.getOperator());
                merchant$.setPhone(merchant.getPhone());
                creation = false;
            }
        }
        Notification notification;
        try {
            merchantRepository.save(merchant$);
            notification = Notification.info("Le marchand <b>" + merchant$.getCode() +"</b> a été " + (creation ? "ajouté." : "modifié."));
            logRepository.save(Log.info(notification.getMessage()));
            creation = true;
            merchant$ = new Merchant();
        } catch (Exception e){
            notification = Notification.error("Erreur lors de la " + (creation ? "création" : "modification") + " du marchand <b>[ " + merchant$.getCode() + " ]</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
            logger.error(notification.getMessage(), e);
        }

        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("merchant", merchant$);
            view.getModel().put("notification", notification);
            view.setViewName("admin/merchant/save");
        }else{
            attributes.addFlashAttribute("notification", notification);
            view.setViewName("redirect:/merchant/list");
        }
        return view;
    }

    @Override
    public RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            merchantRepository.deleteAllById(ids);
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des marchands.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
            logger.error(notification.getMessage(), e);
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/merchant/list", true);
    }

    @Override
    public Notification toggleById(long id){
        Notification notification = Notification.error("Marchand introuvable.");
        try {
            Merchant merchant = merchantRepository.findById(id).orElse(null);
            if(merchant != null){
                merchant.setEnabled(!merchant.getEnabled());
                merchant = merchantRepository.save(merchant);
                notification = Notification.info("Le marchand <b>" + merchant.getCode() + "</b> a été " + (merchant.getEnabled() ? "activé" : "désactivé") + " avec succès.");
                logRepository.save(Log.info(notification.getMessage()));
            }
        }catch (Exception e){
            notification = Notification.error("Erreur lors du changement de statut du marchand d'identifiant <b>" + id + "</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
            logger.error(notification.getMessage(), e);
        }
        return notification;
    }
}
