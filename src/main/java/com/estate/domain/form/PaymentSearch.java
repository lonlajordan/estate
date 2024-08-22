package com.estate.domain.form;

import com.estate.domain.enumaration.Mode;
import com.estate.domain.enumaration.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentSearch {
    private Mode mode;
    private Status status;
    private LocalDate startDate;
    private LocalDate endDate;
}
