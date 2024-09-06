package com.estate.domain.form;

import com.estate.domain.enumaration.SettingType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SettingForm {
    @NotNull
    private Long id;
    private String name;
    @NotNull
    private SettingType type;
    @NotBlank
    private String value;
}
