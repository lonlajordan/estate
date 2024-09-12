package com.estate.domain.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
public class StandingForm {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Min(value = 1)
    private Integer rent;
    @PositiveOrZero
    private Integer caution;
    @PositiveOrZero
    private Integer repair;
}
