package com.estate.domain.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PartnerForm {
    @NotBlank
    private String name;
}
