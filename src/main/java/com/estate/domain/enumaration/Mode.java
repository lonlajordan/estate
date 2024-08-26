package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum Mode {
    BANK("Banque"),
    CASH("Cash"),
    MTN("MTN Mobile Money"),
    ORANGE("Orange Money"),
    PAYPAL("PayPal");

    private final String name;

    Mode(String name) {
        this.name = name;
    }
}
