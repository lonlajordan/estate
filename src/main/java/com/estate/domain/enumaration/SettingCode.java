package com.estate.domain.enumaration;

import lombok.Getter;

@Getter
public enum SettingCode {
    ORANGE_MONEY_MERCHANT_CODE("Code marchant Orange Money"),
    ORANGE_MONEY_MERCHANT_NAME("Nom du marchant Orange Money"),
    MTN_MOBILE_MONEY_MERCHANT_CODE("Code marchant MTN Mobile Money"),
    MTN_MOBILE_MONEY_MERCHANT_NAME("Nom du marchant MTN Mobile Money"),
    BANK_NAME("Nom de la banque"),
    BANK_ACCOUNT_NAME("Titulaire du compte bancaire"),
    BANK_ACCOUNT_NUMBER("Numéro du compte bancaire"),
    PAYPAL_LINK("Lien de paiement PayPal");

    private final String name;

    SettingCode(String name) {
        this.name = name;
    }
}
