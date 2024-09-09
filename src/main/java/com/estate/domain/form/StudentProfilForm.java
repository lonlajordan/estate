package com.estate.domain.form;

import com.estate.domain.annotation.FileSize;
import com.estate.domain.enumaration.Grade;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StudentProfilForm {
    private Long id;

    @NotBlank
    private String school;
    @NotBlank
    private String specialities;
    @NotNull
    private Grade grade;
    @FileSize(extensions = {"pdf", "png", "jpg", "jpeg"}, max = 2 * 1024 * 1024)
    private MultipartFile studentCardFile;

    @NotBlank
    @Email
    private String email;
    @NotNull
    @Valid
    private Phone phone;
    @NotNull
    @Valid
    private Phone mobile;

    @NotBlank
    private String firstParentAddress;

    @NotBlank
    private String secondParentAddress;
}

