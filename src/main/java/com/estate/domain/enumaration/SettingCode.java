package com.estate.domain.enumaration;

import com.estate.domain.converter.EnumConverter;
import lombok.Getter;

@Getter
public enum SettingCode {
    ORANGE_MONEY_MERCHANT_CODE("Code marchant Orange Money", SettingType.TEXT),
    ORANGE_MONEY_MERCHANT_NAME("Nom du marchant Orange Money", SettingType.TEXT),
    MTN_MOBILE_MONEY_MERCHANT_CODE("Code marchant MTN Mobile Money", SettingType.TEXT),
    MTN_MOBILE_MONEY_MERCHANT_NAME("Nom du marchant MTN Mobile Money", SettingType.TEXT),
    BANK_NAME("Nom de la banque", SettingType.TEXT),
    BANK_ACCOUNT_NAME("Titulaire du compte bancaire", SettingType.TEXT),
    BANK_ACCOUNT_NUMBER("Numéro du compte bancaire", SettingType.TEXT),
    PAYPAL_LINK("Lien de paiement PayPal", SettingType.TEXT),
    TELEPHONE_PUBLIC("Numéro de téléphone public", SettingType.TEXT),
    EMAIL_PUBLIC("Adresse e-mail publique", SettingType.EMAIL),
    ADDRESS_PUBLIC("Localisation", SettingType.TEXT);

    private final String name;
    private final SettingType type;

    SettingCode(String name, SettingType type) {
        this.name = name;
        this.type = type;
    }

    public static class Converter extends EnumConverter<SettingCode> {
        public Converter(Class<SettingCode> clazz) {
            super(clazz);
        }
    }
}
