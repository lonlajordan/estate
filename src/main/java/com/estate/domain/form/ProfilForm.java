package com.estate.domain.form;

import com.estate.domain.enumaration.Gender;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ProfilForm {
    @NotBlank
    private String id;
    @NotBlank
    private String firstName;
    private String lastName;
    @NotNull
    private Gender gender;
    @NotBlank
    @Email
    private String email;
    @NotNull
    @Valid
    private Phone phone = new Phone();
    @NotNull
    @Valid
    private Phone mobile = new Phone();
}
