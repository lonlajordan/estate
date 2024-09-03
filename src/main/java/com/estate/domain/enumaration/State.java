package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum State {
    INITIATED("En attente"),
    ACTIVATED("En cours"),
    EXPIRED("Expiré");

    private final String name;

    State(String name) {
        this.name = name;
    }
}
