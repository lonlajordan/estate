package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_ADMIN("Administrateur"),
    ROLE_MONITOR("Moniteur"),
    ROLE_USER("Utilisateur");

    private final String name;

    Role(String name) {
        this.name = name;
    }
}
