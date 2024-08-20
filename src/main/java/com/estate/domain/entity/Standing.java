package com.estate.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Standing extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name = "";
    @Column(nullable = false)
    private int rent;
    @Column(nullable = false)
    private int caution;
    @Column(nullable = false)
    private int repair;

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.name != null) this.name = this.name.replaceAll(" ", "").toUpperCase();
    }
}
