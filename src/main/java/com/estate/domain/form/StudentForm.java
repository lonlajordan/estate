package com.estate.domain.form;

import com.estate.domain.enumaration.Gender;
import com.estate.domain.enumaration.Grade;
import com.estate.domain.enumaration.Relationship;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class StudentForm {
    private Long id;
    @NotBlank
    private String firstName;
    private String lastName;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    @NotBlank
    private String placeOfBirth;
    @NotNull
    private Gender gender;
    private MultipartFile birthCertificatFile;
    private MultipartFile cniRectoFile;
    private MultipartFile cniVersoFile;


    @NotBlank
    private String school;
    @NotBlank
    private String specialities;
    @NotNull
    private Grade grade;
    private MultipartFile studentCardFile;

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String phone;

    @NotNull
    private Relationship firstParentRelation;
    @NotBlank
    private String firstParentName;
    @NotBlank
    private String firstParentAddress;
    @NotBlank
    private String firstParentPhone;
    @NotBlank
    @Email
    private String firstParentEmail;

    @NotNull
    private Relationship secondParentRelation;
    @NotBlank
    private String secondParentName;
    @NotBlank
    private String secondParentAddress;
    @NotBlank
    private String secondParentPhone;
    @NotBlank
    @Email
    private String secondParentEmail;

}
