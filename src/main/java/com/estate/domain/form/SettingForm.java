package com.estate.domain.form;

import com.estate.domain.enumaration.SettingCode;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SettingForm {
    @NotBlank
    private String id;
    @NotNull
    private SettingCode code;
    @NotBlank
    private String value;
}
