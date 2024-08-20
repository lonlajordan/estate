package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Country {
    GAB("Gabon");

    private final String name;

    Country(String name) {
        this.name = name;
    }

}
