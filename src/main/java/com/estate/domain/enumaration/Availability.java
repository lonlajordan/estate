package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Availability {
    FREE("Disponible"),
    OCCUPIED("Occupé"),
    LIBERATION("En cours de libération");

    private final String name;

    Availability(String name) {
        this.name = name;
    }

}
