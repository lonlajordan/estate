package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Testimonial;
import com.estate.domain.form.TestimonialForm;

import java.util.List;

public interface TestimonialService {

    List<Testimonial> findAll();

    Notification save(TestimonialForm testimonialForm);
}
