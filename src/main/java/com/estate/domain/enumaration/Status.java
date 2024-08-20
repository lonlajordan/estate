package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("En attente"),
    CANCELLED("Annulée"),
    CONFIRMED("Confirmé"),
    APPROVED("Approuvée"),
    REJECTED("Rejetée");

    private final String name;

    Status(String name) {
        this.name = name;
    }

}
