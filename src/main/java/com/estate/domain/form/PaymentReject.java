package com.estate.domain.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PaymentReject {
    @NotNull
    private Long id;
    @NotBlank
    private String comment;
}
