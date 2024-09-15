package com.estate.repository;

import com.estate.domain.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TestimonialRepository extends JpaRepository<Testimonial, Long>, JpaSpecificationExecutor<Testimonial> {
}
