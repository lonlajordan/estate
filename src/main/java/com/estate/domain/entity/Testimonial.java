package com.estate.domain.entity;

import com.estate.domain.form.TestimonialForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Testimonial extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String profession;
    private String message;
    @OneToOne
    private Picture picture;


    public Testimonial toTestimonial(TestimonialForm testimonialForm){
        Testimonial testimonial = new Testimonial();
        testimonial.setName(testimonialForm.getName());
        testimonial.setMessage(testimonialForm.getName());
        testimonial.setProfession(testimonialForm.getProfession());

        return testimonial;
    }

}
