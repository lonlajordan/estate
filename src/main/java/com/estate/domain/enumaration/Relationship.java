package com.estate.domain.enumaration;

import com.estate.domain.converter.EnumConverter;
import lombok.Getter;

@Getter
public enum Relationship {
    FATHER("Père"),
    MOTHER("Mère"),
    UNCLE("Oncle"),
    AUNT("Tante"),
    TUTOR("Tuteur"),
    TUTOR2("Tutrice");

    private final String name;

    Relationship(String name) {
        this.name = name;
    }

    public static class Converter extends EnumConverter<Relationship> {
        public Converter() {
            super(Relationship.class);
        }
    }
}
