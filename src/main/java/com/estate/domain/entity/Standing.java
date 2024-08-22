package com.estate.domain.entity;

import com.estate.domain.form.StandingForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "UK_NAME", columnNames = { "name"})})
public class Standing extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name = "";
    @Column(nullable = false)
    private int rent;
    @Column(nullable = false)
    private int caution;
    @Column(nullable = false)
    private int repair;

    public StandingForm toForm(){
        StandingForm form = new StandingForm();
        form.setId(id);
        form.setName(name);
        form.setRent(rent);
        form.setCaution(caution);
        form.setRepair(repair);
        return form;
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.name != null) this.name = this.name.replaceAll(" ", "").toUpperCase();
    }
}
