package com.example.bankcards.util;

public final class MaskingUtil {
    private MaskingUtil() {}
    public static String maskLast4(String last4) {
        if (last4 == null || last4.length() != 4) return "**** **** **** ****";
        return "**** **** **** " + last4;
    }
}
