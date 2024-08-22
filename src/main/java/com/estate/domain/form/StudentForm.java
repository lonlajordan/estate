package com.estate.domain.form;

import com.estate.domain.enumaration.Gender;
import com.estate.domain.enumaration.Grade;
import lombok.Data;
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
    private Gender gender;
    @NotBlank
    private String placeOfBirth;
    @NotNull
    private LocalDate dateOfBirth;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String school;
    @NotBlank
    private String specialities;
    @NotNull
    private Grade grade;

    @NotBlank
    private String firstParentName;
    @NotBlank
    private String firstParentAddress;
    @NotBlank
    private String firstParentPhone;
    @NotBlank
    @Email
    private String firstParentEmail;

    @NotBlank
    private String secondParentName;
    @NotBlank
    private String secondParentAddress;
    @NotBlank
    private String secondParentPhone;
    @NotBlank
    @Email
    private String secondParentEmail;

    private MultipartFile cniRectoFile;
    private MultipartFile cniVersoFile;
    private MultipartFile birthCertificatFile;
    private MultipartFile studentCardFile;
}
