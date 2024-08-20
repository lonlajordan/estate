package com.estate.domain.entity;

import com.estate.domain.enumaration.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_country_name", columnNames = {"country", "name"})})
public class Province extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated
    private Country country = Country.GAB;
    @NotBlank(message = "champs requis")
    @Column(nullable = false)
    private String name;


    public Province(String name) {
        this.name = name;
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.name != null) this.name = this.name.trim().toUpperCase();
    }
}
