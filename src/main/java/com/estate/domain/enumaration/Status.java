package com.estate.domain.enumaration;

import com.estate.domain.converter.EnumConverter;
import lombok.Getter;

@Getter
public enum Status {
    INITIATED("Initié", "bg-warning"),
    SUBMITTED("Soumis", "bg-info"),
    CONFIRMED("Confirmé", "bg-success"),
    CANCELLED("Annulé", "bg-danger");

    private final String name;
    private final String background;

    Status(String name, String background) {
        this.name = name;
        this.background = background;
    }

    public static class Converter extends EnumConverter<Status> {
        public Converter() {
            super(Status.class);
        }
    }

}
