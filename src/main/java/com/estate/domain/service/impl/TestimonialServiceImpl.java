package com.estate.domain.service.impl;

import com.estate.domain.entity.Log;
import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Testimonial;
import com.estate.domain.form.TestimonialForm;
import com.estate.domain.service.face.TestimonialService;
import com.estate.repository.LogRepository;
import com.estate.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final LogRepository logRepository;

    @Override
    public List<Testimonial> findAll(){
        return testimonialRepository.findAll();
    }

    @Override
    public Notification save(TestimonialForm form){
        boolean creation = form.getId() == null;
        Notification notification;
        Testimonial testimonial = creation ? new Testimonial() : testimonialRepository.findById(form.getId()).orElse(null);
        if(testimonial == null) return Notification.error("Témoignage introuvable");
        testimonial.setName(form.getName());
        testimonial.setProfession(form.getProfession());
        testimonial.setMessage(form.getMessage());
        if(form.getPicture() != null && !form.getPicture().isEmpty()){
            long date = System.currentTimeMillis();
            String extension;
            File root = new File("documents");
            if (!root.exists() && !root.mkdirs()) return Notification.error("Impossible de créer le dossier de sauvegarde des documents.");
            File picture;
            if(StringUtils.isNotBlank(testimonial.getPicture())){
                picture = new File(testimonial.getPicture());
                try {
                    if(picture.exists()) FileUtils.deleteQuietly(picture);
                } catch (Exception ignored) {}
            }
            try {
                extension = FilenameUtils.getExtension(form.getPicture().getOriginalFilename());
                picture = new File(root.getAbsolutePath() + File.separator + "testimonial-" + date + "." + extension);
                form.getPicture().transferTo(picture);
                testimonial.setPicture(root.getName() + File.separator + picture.getName());
            } catch (IOException e) {
                log.error("unable to write testimonial logo file", e);
                return Notification.error("Impossible d'enregistrer l'image d'un témoin.");
            }
        }
        try {
            testimonialRepository.save(testimonial);
            notification = Notification.info("Un témoignage a été " + (creation ? "ajouté." : "modifié."));
            logRepository.save(Log.info(notification.getMessage()));
        } catch (Throwable e){
            notification = Notification.error("Erreur lors de la modification du témoignage.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return notification;
    }

    @Override
    public Optional<Testimonial> findById(long id) {
        return testimonialRepository.findById(id);
    }

    @Override
    public Notification deleteById(long id, HttpServletRequest request) {
        Notification notification;
        Testimonial testimonial = testimonialRepository.findById(id).orElse(null);
        if(testimonial == null) return Notification.error("Témoignage introuvable");
        try {
            testimonialRepository.deleteById(id);
            if(StringUtils.isNotBlank(testimonial.getPicture())){
                File picture = new File(testimonial.getPicture());
                try {
                    if(picture.exists()) FileUtils.deleteQuietly(picture);
                } catch (Exception ignored) {}
            }
            notification = Notification.info("Le témoignage a été supprimé");
            logRepository.save(Log.info(notification.getMessage()));
        }catch (Throwable e){
            notification = Notification.error("Erreur lors de la suppression du témoignage.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return notification;
    }
}
