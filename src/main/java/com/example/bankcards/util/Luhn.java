package com.example.bankcards.util;

import java.security.SecureRandom;

public final class Luhn {
    private static final SecureRandom RND = new SecureRandom();

    private Luhn() {}

    public static int checksum(String digits) {
        if (digits == null || !digits.matches("\\d+")) {
            throw new IllegalArgumentException("ЫЫ ТАК НЕЛЬЗЯЯЯ!!!!! (((((");
        }
        int sum = 0;
        boolean doubleIt = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return sum % 10;
    }

    public static boolean isValid(String digits) {
        return checksum(digits) == 0;
    }

    public static String random16(boolean forceValid) {
        int len = 16;
        int[] d = new int[len];
        for (int i = 0; i < len - 1; i++) d[i] = RND.nextInt(10);

        if (forceValid) {
            int sum = 0;
            boolean doubleIt = true;
            for (int i = len - 2; i >= 0; i--) {
                int x = d[i];
                if (doubleIt) {
                    x *= 2;
                    if (x > 9) x -= 9;
                }
                sum += x;
                doubleIt = !doubleIt;
            }
            int mod = sum % 10;
            d[len - 1] = (10 - mod) % 10;
        } else {
            d[len - 1] = RND.nextInt(10);
        }

        StringBuilder sb = new StringBuilder(len);
        for (int x : d) sb.append(x);
        return sb.toString();
    }
}
