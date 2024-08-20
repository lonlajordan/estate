package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum GameType {
    QUIZ("Quiz ordinaire"),
    BATTLE("Battle"),
    MYSTERY("Question Mystère"),
    MEGA_QUIZ("Méga quiz"),
    DRIVE_TEST("Test conducteur");

    private final String name;

    GameType(String name) {
        this.name = name;
    }
}
