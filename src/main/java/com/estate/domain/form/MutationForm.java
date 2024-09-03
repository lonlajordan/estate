package com.estate.domain.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class MutationForm {
    @NotNull
    private Long leaseId;
    @NotNull
    private Long housingId;
    private Boolean refund;
    @PositiveOrZero
    private int amount;
}
