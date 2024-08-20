package com.estate.domain.service.impl;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Advertisement;
import com.estate.domain.entity.Log;
import com.estate.domain.enumaration.Level;
import com.estate.domain.enumaration.MediaType;
import com.estate.domain.service.face.AdvertisementService;
import com.estate.repository.AdvertisementRepository;
import com.estate.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final LogRepository logRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public long countAdvertisements() {
        return advertisementRepository.count();
    }

    @Override
    public Optional<Advertisement> findById(long id) {
        return advertisementRepository.findById(id);
    }

    @Override
    public List<Advertisement> findAll(){
        return advertisementRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public ModelAndView save(Advertisement advertisement, MultipartFile file, Boolean multiple, RedirectAttributes attributes) {
        Advertisement advertisement$ = advertisement;
        boolean creation = true;
        if(advertisement.getId() != null) {
            Optional<Advertisement> _advertisement = advertisementRepository.findById(advertisement.getId());
            if (_advertisement.isPresent()) {
                advertisement$ = _advertisement.get();
                advertisement$.setTitle(advertisement.getTitle());
                advertisement$.setExternLink(advertisement.getExternLink());
                creation = false;
            }
        }
        Notification notification = Notification.info();
        try {
            advertisement$ = advertisementRepository.save(advertisement$);
            notification.setMessage("Une publicité a été " + (creation ? "ajoutée." : "modifiée."));
            logRepository.save(Log.info(notification.getMessage()));
            if(creation){
                String fileName = "advertisements/" + new Date().getTime() + "-" + advertisement$.getId() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
                String link = "";
                advertisement$.setFileName(fileName);
                advertisement$.setLink(link);
                advertisement$.setType(new MimetypesFileTypeMap().getContentType(fileName).toLowerCase().contains("image") ? MediaType.IMAGE : MediaType.VIDEO);
                advertisementRepository.save(advertisement$);
            }
            creation = true;
            advertisement$ = new Advertisement();
        } catch (Exception e){
            notification.setType(Level.ERROR);
            notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " de la publicité <b>[ " + advertisement$.getId() + " ]</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("creation", creation);
            view.getModel().put("notification", notification);
            view.getModel().put("advertisement", advertisement$);
            view.setViewName("admin/advertisement/save");
        }else{
            attributes.addFlashAttribute("notification", notification);
            view.setViewName("redirect:/advertisement/list");
        }
        return view;
    }

    @Override
    public RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        List<Advertisement> advertisements = advertisementRepository.findAllByIdIn(new HashSet<>(ids));
        int n = 0;
        for(Advertisement advertisement: advertisements){
            try {
                advertisementRepository.delete(advertisement);
                n++;
            }catch (Exception e){
                notification = Notification.error("Erreur lors de la suppression.");
                logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
            }
        }
        if(n > 0){
            String plural = n > 1 ? "s" : "";
            notification.setMessage( n + " publicité" + plural + " supprimée" + plural +".");
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/advertisement/list", true);
    }

    @Override
    public List<Advertisement> findRandomly(int n) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Advertisement> cq = cb.createQuery(Advertisement.class);
        Root<Advertisement> order = cq.from(Advertisement.class);
        cq.where(cb.equal(order.get("type"), MediaType.IMAGE));
        cq.orderBy(cb.asc(cb.function("RAND", null)));
        TypedQuery<Advertisement> query = em.createQuery(cq).setMaxResults(n).setFirstResult(0);
        return query.getResultList();
    }
}
