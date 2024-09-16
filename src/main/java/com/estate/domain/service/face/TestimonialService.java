package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Testimonial;
import com.estate.domain.form.TestimonialForm;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface TestimonialService {

    List<Testimonial> findAll();

    Notification save(TestimonialForm testimonialForm);

    Notification deleteById(long id, HttpServletRequest request);

    Optional<Testimonial> findById(long id);
}
