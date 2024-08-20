package com.estate.domain.entity;

import com.estate.domain.enumaration.Availability;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


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

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.name != null) this.name = this.name.replaceAll(" ", "").toUpperCase();
    }

    @PreRemove
    public void beforeDelete(){
    }
}
