package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Operator {
    MC("MobiCash Gabon"),
    AM("Airtel Money Gabon"),
    VM("VISA/Mastercard"),
    BP("Bamboo Pay");

    private final String name;

    Operator(String name) {
        this.name = name;
    }

}
