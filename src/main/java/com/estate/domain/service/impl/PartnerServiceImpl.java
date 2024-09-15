package com.estate.domain.service.impl;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Partner;
import com.estate.domain.entity.Testimonial;
import com.estate.domain.form.PartnerForm;
import com.estate.domain.form.TestimonialForm;
import com.estate.domain.service.face.PartnerService;
import com.estate.domain.service.face.TestimonialService;
import com.estate.repository.PartnerRepository;
import com.estate.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    public List<Partner> findAll(){
        return partnerRepository.findAll();
    }

    public Notification save(PartnerForm partnerForm){
        Notification notification;
        Partner partner = new Partner();
        partner = partner.toPartner(partnerForm);
        partnerRepository.save(partner);
        notification = Notification.info("Le partenaire a été ajouté avec succès");

        return notification;

    }
}
