package com.estate.domain.service.impl;

import com.estate.domain.entity.Log;
import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Partner;
import com.estate.domain.form.PartnerForm;
import com.estate.domain.service.face.PartnerService;
import com.estate.repository.LogRepository;
import com.estate.repository.PartnerRepository;
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
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final LogRepository logRepository;

    @Override
    public List<Partner> findAll(){
        return partnerRepository.findAll();
    }

    @Override
    public Notification save(PartnerForm form){
        boolean creation = form.getId() == null;
        Notification notification;
        Partner partner = creation ? new Partner() : partnerRepository.findById(form.getId()).orElse(null);
        if(partner == null) return Notification.error("Partenaire introuvable");
        partner.setName(form.getName());
        if(form.getPicture() != null && !form.getPicture().isEmpty()){
            long date = System.currentTimeMillis();
            String extension;
            File root = new File("documents");
            if (!root.exists() && !root.mkdirs()) return Notification.error("Impossible de créer le dossier de sauvegarde des documents.");
            File picture;
            if(StringUtils.isNotBlank(partner.getLogo())){
                picture = new File(partner.getLogo());
                try {
                    if(picture.exists()) FileUtils.deleteQuietly(picture);
                } catch (Exception ignored) {}
            }
            try {
                extension = FilenameUtils.getExtension(form.getPicture().getOriginalFilename());
                picture = new File(root.getAbsolutePath() + File.separator + "partner-" + date + "." + extension);
                form.getPicture().transferTo(picture);
                partner.setLogo(root.getName() + File.separator + picture.getName());
            } catch (IOException e) {
                log.error("unable to write partner logo file", e);
                return Notification.error("Impossible d'enregistrer le logo du partenaire.");
            }
        }
        try {
            partnerRepository.save(partner);
            notification = Notification.info("Un partenaire a été " + (creation ? "ajouté." : "modifié."));
            logRepository.save(Log.info(notification.getMessage()));
        } catch (Throwable e){
            notification = Notification.error("Erreur lors de la modification du partenaire <b>" + partner.getName() + "</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return notification;
    }

    @Override
    public Optional<Partner> findById(long id) {
        return partnerRepository.findById(id);
    }

    @Override
    public Notification deleteById(long id, HttpServletRequest request) {
        Notification notification;
        Partner partner = partnerRepository.findById(id).orElse(null);
        if(partner == null) return Notification.error("Partenaire introuvable");
        try {
            partnerRepository.deleteById(id);
            if(StringUtils.isNotBlank(partner.getLogo())){
                File picture = new File(partner.getLogo());
                try {
                    if(picture.exists()) FileUtils.deleteQuietly(picture);
                } catch (Exception ignored) {}
            }
            notification = Notification.info("Le partenaire <b>" + partner.getName() + "</b> a été supprimé");
            logRepository.save(Log.info(notification.getMessage()));
        }catch (Throwable e){
            notification = Notification.error("Erreur lors de la suppression du partenaire <b>" + partner.getName() + "</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return notification;
    }
}
