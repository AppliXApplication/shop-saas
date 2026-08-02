package com.applix.shop.auth;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Легаси-приложение хранит пароли как hex(SHA-1(rawPassword)) — без соли.
 * Проверено опытным путём (debug-эндпоинт перебора вариантов формулы,
 * теперь удалён) на реальном пользователе. Соль ("Mary has one cat" /
 * файл key.key) для этого не используется — служит другой цели.
 * <p>
 * Это слабый алгоритм по современным меркам (SHA-1 без соли — уязвим к
 * radужным таблицам), но менять формат сейчас нельзя — сломает вход для
 * всех существующих пользователей легаси-системы. Миграцию на bcrypt
 * стоит сделать отдельным шагом: при успешном логине пересчитывать
 * пароль в bcrypt и постепенно переводить базу.
 */
@Component
public class LegacyShaPasswordVerifier {

    public boolean matches(String rawPassword, String storedHashHex) {
        if (rawPassword == null || storedHashHex == null) {
            return false;
        }
        String computed = hash(rawPassword);
        return MessageDigest.isEqual(
                computed.getBytes(StandardCharsets.UTF_8),
                storedHashHex.trim().toLowerCase().getBytes(StandardCharsets.UTF_8)
        );
    }

    private String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 недоступен в текущей JVM", e);
        }
    }
}

