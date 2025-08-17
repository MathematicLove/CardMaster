package com.example.bankcards.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CryptoUtil {

    private final byte[] keyBytes;
    private SecretKey key;
    private final SecureRandom secureRandom = new SecureRandom();

    public CryptoUtil(@Value("${app.crypto.key}") String keyString) {
        this.keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
    }

    @PostConstruct
    public void init() {
        byte[] use = keyBytes;
        if (use.length < 32) {
            try {
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(256);
                key = kg.generateKey();
                return;
            } catch (Exception ignored) {}
        }
        key = new SecretKeySpec(use.length >= 32 ? slice(use, 32) : use, "AES");
    }

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            ByteBuffer bb = ByteBuffer.allocate(12 + ct.length);
            bb.put(iv);
            bb.put(ct);
            return Base64.getEncoder().encodeToString(bb.array());
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка шифрования", e);
        }
    }

    public String decrypt(String encoded) {
        try {
            byte[] all = Base64.getDecoder().decode(encoded);
            ByteBuffer bb = ByteBuffer.wrap(all);
            byte[] iv = new byte[12];
            bb.get(iv);
            byte[] ct = new byte[bb.remaining()];
            bb.get(ct);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            byte[] pt = cipher.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка расшифровки", e);
        }
    }

    private static byte[] slice(byte[] src, int len) {
        byte[] out = new byte[len];
        System.arraycopy(src, 0, out, 0, len);
        return out;
    }
}
