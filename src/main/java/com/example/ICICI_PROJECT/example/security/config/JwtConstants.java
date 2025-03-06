package com.example.ICICI_PROJECT.example.security.config;

public class JwtConstants {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String MOBILE_NUMBER = "mobileNumber";
    public static final String ORGANIZATION = "bankCode";
    public static final String IS_PREMIUM = "hasPremiumSubscription";
    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String GUID = "guid";

    private JwtConstants() {
        throw new IllegalStateException("Utility class");
    }
}
