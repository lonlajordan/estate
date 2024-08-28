package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Status {
    INITIATED("Initié"),
    SUBMITTED("Soumis"),
    CONFIRMED("Confirmé"),
    CANCELLED("Annulé");

    private final String name;

    Status(String name) {
        this.name = name;
    }

}
