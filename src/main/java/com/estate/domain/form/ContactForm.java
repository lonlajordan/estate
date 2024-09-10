package com.estate.domain.form;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ContactForm {

    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @Valid
    private Phone phone;
}
