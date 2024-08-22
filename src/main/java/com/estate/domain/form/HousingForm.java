package com.estate.domain.form;

import com.estate.domain.enumaration.Availability;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HousingForm {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private Long standingId;
    @NotNull
    private Availability status;
}
