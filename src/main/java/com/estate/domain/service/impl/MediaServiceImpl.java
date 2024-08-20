package com.estate.domain.service.impl;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Media;
import com.estate.domain.enumaration.Level;
import com.estate.domain.enumaration.MediaType;
import com.estate.domain.service.face.MediaService;
import com.estate.repository.LogRepository;
import com.estate.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.activation.MimetypesFileTypeMap;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final LogRepository logRepository;

    @Override
    public Optional<Media> findById(long id) {
        return mediaRepository.findById(id);
    }

    public List<Media> findAll(){
        return mediaRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public ModelAndView save(Media media, MultipartFile file, Boolean multiple, RedirectAttributes attributes) {
        Media media$ = media;
        boolean creation = true;
        if(media.getId() != null) {
            Optional<Media> _media = mediaRepository.findById(media.getId());
            if (_media.isPresent()) {
                media$ = _media.get();
                media$.setTitle(media.getTitle());
                creation = false;
            }
        }
        Notification notification = Notification.info();
        try {
            media$ = mediaRepository.save(media$);
            notification.setMessage("Un média a été " + (creation ? "ajouté." : "modifié."));
            logRepository.save(Log.info(notification.getMessage()));
            if(creation){
                String fileName = "medias/" + new Date().getTime() + "-" + media$.getId() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
                String link = "";
                media$.setFileName(fileName);
                media$.setLink(link);
                String mimeType = new MimetypesFileTypeMap().getContentType(fileName).toLowerCase();
                if(mimeType.contains("image")){
                    media$.setType(MediaType.IMAGE);
                }else if(mimeType.contains("video")){
                    media$.setType(MediaType.VIDEO);
                }else if(mimeType.contains("audio")){
                    media$.setType(MediaType.AUDIO);
                }else{
                    media$.setType(MediaType.DOCUMENT);
                }
                mediaRepository.save(media$);
            }
            creation = true;
            media$ = new Media();
        } catch (Exception e){
            notification.setType(Level.ERROR);
            notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " du média <b>[ " + media$.getId() + " ]</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("creation", creation);
            view.getModel().put("notification", notification);
            view.getModel().put("media", media$);
            view.setViewName("admin/media/save");
        }else{
            attributes.addFlashAttribute("notification", notification);
            view.setViewName("redirect:/media/list");
        }
        return view;
    }

    @Override
    public RedirectView deleteAllByIds(List<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        List<Media> medias = mediaRepository.findAllByIdIn(new HashSet<>(ids));
        int n = 0;
        for(Media media: medias){
            try {
                mediaRepository.delete(media);
                n++;
            }catch (Exception e){
                notification = Notification.error("Erreur lors de la suppression.");
                logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
            }
        }
        if(n > 0){
            String plural = n > 1 ? "s" : "";
            notification.setMessage( n + " média" + plural + " supprimé" + plural +".");
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/media/list", true);
    }

    @Override
    public Page<Media> mediaList(int p) {
        Pageable pageable = PageRequest.of(p  - 1, 1000);
        return mediaRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
