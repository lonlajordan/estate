package com.estate.domain.entity;

import com.estate.domain.enumaration.Mode;
import com.estate.domain.enumaration.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Payment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rent;
    private int months;
    private int caution;
    private int repair;
    @ManyToOne(optional = false)
    private Standing standing;
    @Enumerated(EnumType.STRING)
    private Mode mode = Mode.CASH;
    @ManyToOne
    private User validator;
    @ManyToOne(optional = false)
    private Student student;
    @ManyToOne
    private Housing desiderata;
    private String proof;

    private Status status = Status.PENDING;
}
