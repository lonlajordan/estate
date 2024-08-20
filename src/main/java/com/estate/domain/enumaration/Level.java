package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Level {
    WARN("Avertissement", "alert-warning"),
    ERROR("Erreur", "alert-danger"),
    INFO("Information", "alert-success");

    private final String name;
    private final String style;

    Level(String name, String style) {
        this.name = name;
        this.style = style;
    }

}
