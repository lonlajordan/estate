package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Relationship {
    FATHER("Père"),
    MOTHER("Mère"),
    UNCLE("Oncle"),
    AUNT("Tante"),
    TUTOR("Tuteur");

    private final String name;

    Relationship(String name) {
        this.name = name;
    }
}
