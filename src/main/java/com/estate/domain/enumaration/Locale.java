package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum  Locale {
    AR("Arabe"),
    EN("Anglais"),
    ES("Espagnol"),
    FR("Français"),
    PT("Portugais");

    private final String language;

    Locale(String language) {
        this.language = language;
    }

}
