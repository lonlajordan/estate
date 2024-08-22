package com.estate.domain.entity;

import com.estate.domain.enumaration.Availability;
import com.estate.domain.form.HousingForm;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Optional;


@Setter
@Getter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_name", columnNames = {"name"}))
public class Housing extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne(optional = false)
    private Standing standing;
    @Enumerated(EnumType.STRING)
    private Availability status = Availability.FREE;

    public Housing() {
    }

    public HousingForm toForm(){
        HousingForm form = new HousingForm();
        form.setId(id);
        form.setName(name);
        form.setStandingId(Optional.ofNullable(standing).map(Standing::getId).orElse(null));
        form.setStatus(status);
        return form;
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.name != null) this.name = this.name.replaceAll(" ", "").toUpperCase();
    }

    @PreRemove
    public void beforeDelete(){
    }
}
