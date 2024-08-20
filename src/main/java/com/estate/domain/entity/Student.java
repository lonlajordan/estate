package com.estate.domain.entity;

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
    private String placeOfBirth;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private String cniRecto; // CNI face recto
    private String cniVerso; // CNI face verso
    private String birthCertificat; // Acte de naissance
    private String studentCard; // Carte d'étudiant
    private String school;
    private String speciality1;
    private String speciality2;
    private String speciality3;

    @Column(nullable = false)
    private String email;
    private String password;
    private String phone;
    private char sex = 'M';
    @ManyToOne
    private Housing city;
    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String otp = "";
    @Column(nullable = false)
    private LocalDateTime otpExpiredAt = LocalDateTime.now();
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
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

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.firstName != null) this.firstName = this.firstName.trim().toUpperCase();
        if(this.lastName != null) this.lastName = Arrays.stream(this.lastName.trim().toLowerCase().split("\\s+")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(this.email != null) this.email = this.email.trim();
    }

    @PreRemove
    public void beforeDelete(){
    }
}
