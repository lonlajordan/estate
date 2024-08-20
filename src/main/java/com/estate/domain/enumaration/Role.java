package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_ADMIN("Administrateur"),
    ROLE_MANAGER("Gestionnaire"),
    ROLE_JANITOR("Concierge");

    private final String name;

    Role(String name) {
        this.name = name;
    }
}
