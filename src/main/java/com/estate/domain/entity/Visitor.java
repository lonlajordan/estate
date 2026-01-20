package com.estate.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "UK_EMAIL", columnNames = { "email"})})
public class Visitor extends Auditable {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private String id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String phone;

    public Visitor() {

    }
}
