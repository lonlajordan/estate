package com.estate.domain.entity;

import com.estate.domain.enumaration.Gender;
import com.estate.domain.enumaration.Grade;
import com.estate.domain.enumaration.Relationship;
import com.estate.domain.form.Phone;
import com.estate.domain.form.StudentForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Student extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    @Column(nullable = false)
    private String placeOfBirth = "BABADJOU";
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth = LocalDate.of(1998, Month.AUGUST, 1);
    @Column(nullable = false)
    private String cniRecto = ""; // CNI face recto
    private String cniVerso = ""; // CNI face verso
    private String birthCertificate = ""; // Acte de naissance
    private String studentCard = ""; // Carte d'étudiant
    @Column(nullable = false)
    private String school = "ENSP";
    @Convert(converter = Grade.Converter.class)
    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.L1;
    @Column(nullable = false)
    private String specialities = "Informatique";

    @ManyToOne
    private Housing housing;

    @Column(nullable = false)
    @Convert(converter = Relationship.Converter.class)
    @Enumerated(EnumType.STRING)
    private Relationship firstParentRelation = Relationship.FATHER;
    @Column(nullable = false)
    private String firstParentName = "LONLA EMMANUEL";
    @Column(nullable = false)
    private String firstParentAddress = "MESSASSI";
    @Column(nullable = false)
    private String firstParentPhone = "677432413";
    private String firstParentMobile;
    @Column(nullable = false)
    private String firstParentEmail = "admin@gmail.com";

    @Column(nullable = false)
    @Convert(converter = Relationship.Converter.class)
    @Enumerated(EnumType.STRING)
    private Relationship secondParentRelation = Relationship.MOTHER;
    @Column(nullable = false)
    private String secondParentName = "LONLA ANGELINE";
    @Column(nullable = false)
    private String secondParentAddress = "MESSASSI";
    @Column(nullable = false)
    private String secondParentPhone = "677078633";
    private String secondParentMobile;
    @Column(nullable = false)
    private String secondParentEmail = "admin@gmail.com";


    @Column(nullable = false)
    private String otp = "";
    @Column(nullable = false)
    private LocalDateTime otpExpiredAt = LocalDateTime.now();
    @Column(nullable = false)
    private boolean active = true;
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Payment> payments = new ArrayList<>();

    public Student(){
        this.user = new User();
    }

    @OneToOne
    private Lease currentLease;

    public StudentForm toForm(){
        StudentForm form = new StudentForm();
        form.setId(id);
        form.setFirstName(Optional.ofNullable(user).map(User::getFirstName).orElse(""));
        form.setLastName(Optional.ofNullable(user).map(User::getLastName).orElse(""));
        form.setDateOfBirth(dateOfBirth);
        form.setPlaceOfBirth(placeOfBirth);
        form.setGender(Optional.ofNullable(user).map(User::getGender).orElse(Gender.MALE));

        form.setSchool(school);
        form.setSpecialities(specialities);
        form.setGrade(grade);
        form.setPhone(Phone.parse(Optional.ofNullable(user).map(User::getPhone).orElse("")));
        form.setMobile(Phone.parse(Optional.ofNullable(user).map(User::getMobile).orElse("")));
        form.setEmail(Optional.ofNullable(user).map(User::getEmail).orElse(""));

        form.setFirstParentRelation(firstParentRelation);
        form.setFirstParentName(firstParentName);
        form.setFirstParentAddress(firstParentAddress);
        form.setFirstParentPhone(Phone.parse(firstParentPhone));
        form.setFirstParentMobile(Phone.parse(firstParentMobile));
        form.setFirstParentEmail(firstParentEmail);

        form.setSecondParentRelation(secondParentRelation);
        form.setSecondParentName(secondParentName);
        form.setSecondParentAddress(secondParentAddress);
        form.setSecondParentPhone(Phone.parse(secondParentPhone));
        form.setSecondParentMobile(Phone.parse(secondParentMobile));
        form.setSecondParentEmail(secondParentEmail);
        return form;
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){

    }

}
