package com.estate.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


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
