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
public class Testimonial extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String profession;
    private String message;
    private String picture;

    public TestimonialForm toForm(){
        TestimonialForm form = new TestimonialForm();
        form.setId(id);
        form.setName(name);
        form.setMessage(message);
        form.setProfession(profession);
        return form;
    }

}
