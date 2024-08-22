package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Masculin"),
    FEMALE("Féminin");

    private final String name;

    Gender(String name) {
        this.name = name;
    }
}
