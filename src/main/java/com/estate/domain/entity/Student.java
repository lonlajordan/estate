package com.estate.domain.entity;

import com.estate.domain.enumaration.Gender;
import com.estate.domain.enumaration.Grade;
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
    private String firstName;
    private String lastName;
    @Column(nullable = false)
    private String placeOfBirth;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private String cniRecto; // CNI face recto
    private String cniVerso; // CNI face verso
    private String birthCertificat; // Acte de naissance
    private String studentCard; // Carte d'étudiant
    @Column(nullable = false)
    private String school = "";
    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.L1;
    @Column(nullable = false)
    private String specialities = "";

    @Column(nullable = false)
    private String email;
    private String password;
    @Column(nullable = false)
    private String phone;
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.MALE;
    @ManyToOne
    private Housing housing;

    @Column(nullable = false)
    private String firstParentName;
    @Column(nullable = false)
    private String firstParentAddress;
    @Column(nullable = false)
    private String firstParentPhone;
    @Column(nullable = false)
    private String firstParentEmail;

    @Column(nullable = false)
    private String secondParentName;
    @Column(nullable = false)
    private String secondParentAddress;
    @Column(nullable = false)
    private String secondParentPhone;
    @Column(nullable = false)
    private String secondParentEmail;


    @Column(nullable = false)
    private String otp = "";
    @Column(nullable = false)
    private LocalDateTime otpExpiredAt = LocalDateTime.now();
    @Column(nullable = false)
    private Boolean enabled = true;
    @Column(nullable = false)
    private Boolean deleted = false;
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Payment> payments = new ArrayList<>();

    public String getName(){
        return Stream.of(firstName, lastName).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

    public StudentForm toForm(){
        StudentForm form = new StudentForm();
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setGender(gender);
        form.setPlaceOfBirth(placeOfBirth);
        form.setGrade(grade);
        form.setSchool(school);

        form.setFirstParentName(firstParentName);
        form.setFirstParentAddress(firstParentAddress);
        form.setFirstParentPhone(firstParentPhone);
        form.setFirstParentEmail(firstParentEmail);

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
