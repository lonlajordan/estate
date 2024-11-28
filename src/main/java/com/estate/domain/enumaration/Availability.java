package com.estate.domain.enumaration;

import com.estate.domain.converter.EnumConverter;
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

    public static class Converter extends EnumConverter<Availability> {
        public Converter() {
            super(Availability.class);
        }
    }

}
