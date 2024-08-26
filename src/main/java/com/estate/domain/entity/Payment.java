package com.estate.domain.entity;

import com.estate.domain.enumaration.Mode;
import com.estate.domain.enumaration.Status;
import com.estate.domain.form.PaymentForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Payment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rent;
    private int months = 12;
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


    public PaymentForm toForm(){
        PaymentForm form = new PaymentForm();
        form.setId(id);
        form.setRent(rent);
        form.setMonths(months);
        form.setCaution(caution);
        form.setRepair(repair);
        form.setMode(mode);
        form.setStandingId(Optional.ofNullable(standing).map(Standing::getId).orElse(null));
        form.setStudentId(Optional.ofNullable(student).map(Student::getId).orElse(null));
        form.setDesiderataId(Optional.ofNullable(desiderata).map(Housing::getId).orElse(null));
        return form;
    }
}
