package com.estate.domain.service.impl;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Testimonial;
import com.estate.domain.form.TestimonialForm;
import com.estate.domain.service.face.TestimonialService;
import com.estate.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialRepository testimonialRepository;

    public List<Testimonial> findAll(){
        return testimonialRepository.findAll();
    }

    public Notification save(TestimonialForm testimonialForm){
        Notification notification;
        Testimonial testimonial = new Testimonial();
        testimonial = testimonial.toTestimonial(testimonialForm);
        testimonialRepository.save(testimonial);
        notification = Notification.info("Le témoignage a été ajouté avec succès");

        return notification;

    }
}
