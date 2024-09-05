package com.estate.domain.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MutationForm {
    @NotNull
    private Long leaseId;
    @NotNull
    private Long housingId;
    @NotNull
    private Integer amount = 0;
}
