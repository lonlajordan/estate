package com.estate.domain.enumaration;

import com.estate.domain.converter.EnumConverter;
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

    public static class Converter extends EnumConverter<Status> {
        public Converter() {
            super(Status.class);
        }
    }

}
