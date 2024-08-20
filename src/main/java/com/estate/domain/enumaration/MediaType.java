package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum MediaType {
    AUDIO("Audio"),
    IMAGE("Image"),
    VIDEO("Video"),
    DOCUMENT("Document");

    private final String name;

    MediaType(String name) {
        this.name = name;
    }
}
