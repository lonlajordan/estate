package com.estate.domain.entity;

import com.estate.domain.enumaration.Gender;
import com.estate.domain.enumaration.Grade;
import com.estate.domain.enumaration.Relationship;
import com.estate.domain.form.StudentForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName = "LONLA";
    private String lastName = "Gatien Jordan";
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
    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.L1;
    @Column(nullable = false)
    private String specialities = "Informatique";

    @Column(nullable = false)
    private String email = "jordan@gmail.com";
    private String password;
    @Column(nullable = false)
    private String phone = "695463868";
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.MALE;
    @ManyToOne
    private Housing housing;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Relationship firstParentRelation = Relationship.FATHER;
    @Column(nullable = false)
    private String firstParentName = "LONLA EMMANUEL";
    @Column(nullable = false)
    private String firstParentAddress = "MESSASSI";
    @Column(nullable = false)
    private String firstParentPhone = "677432413";
    @Column(nullable = false)
    private String firstParentEmail = "admin@gmail.com";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Relationship secondParentRelation = Relationship.MOTHER;
    @Column(nullable = false)
    private String secondParentName = "LONLA ANGELINE";
    @Column(nullable = false)
    private String secondParentAddress = "MESSASSI";
    @Column(nullable = false)
    private String secondParentPhone = "677078633";
    @Column(nullable = false)
    private String secondParentEmail = "admin@gmail.com";


    @Column(nullable = false)
    private String otp = "";
    @Column(nullable = false)
    private LocalDateTime otpExpiredAt = LocalDateTime.now();
    @Column(nullable = false)
    private Boolean enabled = false;
    @Column(nullable = false)
    private Boolean deleted = false;
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Payment> payments = new ArrayList<>();

    @OneToOne
    private Lease currentLease;

    public String getName(){
        return Stream.of(firstName, lastName).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
    }

    public String getOneName(){
        String name = getName();
        return name.substring(name.lastIndexOf(" ") + 1);
    }

    public StudentForm toForm(){
        StudentForm form = new StudentForm();
        form.setId(id);
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setDateOfBirth(dateOfBirth);
        form.setPlaceOfBirth(placeOfBirth);
        form.setGender(gender);

        form.setSchool(school);
        form.setSpecialities(specialities);
        form.setGrade(grade);
        form.setPhone(phone);
        form.setEmail(email);

        form.setFirstParentRelation(firstParentRelation);
        form.setFirstParentName(firstParentName);
        form.setFirstParentAddress(firstParentAddress);
        form.setFirstParentPhone(firstParentPhone);
        form.setFirstParentEmail(firstParentEmail);

        form.setSecondParentRelation(secondParentRelation);
        form.setSecondParentName(secondParentName);
        form.setSecondParentAddress(secondParentAddress);
        form.setSecondParentPhone(secondParentPhone);
        form.setSecondParentEmail(secondParentEmail);
        return form;
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.firstName != null) this.firstName = this.firstName.trim().toUpperCase();
        if(this.lastName != null) this.lastName = Arrays.stream(this.lastName.trim().toLowerCase().split("\\s+")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(this.email != null) this.email = this.email.trim();
        if(this.phone != null) this.phone = this.phone.trim();
    }

    @PreRemove
    public void beforeDelete(){
    }
}
