package com.estate.domain.entity;

import com.estate.domain.form.PartnerForm;
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
public class Partner extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String logo;


    public PartnerForm toForm(){
        PartnerForm form = new PartnerForm();
        form.setId(id);
        form.setName(name);
        return form;
    }
}
