
        package com.smartbiz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final CacheManager cacheManager;

    private static final String OTP_CACHE = "otpCache";
    private static final String ATTEMPTS_CACHE = "otpAttemptsCache";
    private static final int MAX_ATTEMPTS = 5;

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateAndStoreOtp(String email) {
        String normalizedEmail = normalizeEmail(email);

        String otp = String.format(
                Locale.ROOT,
                "%06d",
                RANDOM.nextInt(1_000_000)
        );

        Cache otpCache = getCache(OTP_CACHE);
        Cache attemptsCache = getCache(ATTEMPTS_CACHE);

        otpCache.put(normalizedEmail, otp);
        attemptsCache.evict(normalizedEmail);

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        if (email == null || email.isBlank()
                || otp == null || !otp.matches("\\d{6}")) {
            return false;
        }

        String normalizedEmail = normalizeEmail(email);

        Cache otpCache = getCache(OTP_CACHE);
        Cache attemptsCache = getCache(ATTEMPTS_CACHE);

        String storedOtp = otpCache.get(
                normalizedEmail,
                String.class
        );

        if (storedOtp == null) {
            return false;
        }

        Integer attempts = attemptsCache.get(
                normalizedEmail,
                Integer.class
        );

        int currentAttempts = attempts == null ? 0 : attempts;

        if (currentAttempts >= MAX_ATTEMPTS) {
            clearOtp(normalizedEmail);
            return false;
        }

        if (storedOtp.equals(otp)) {
            return true;
        }

        int updatedAttempts = currentAttempts + 1;

        if (updatedAttempts >= MAX_ATTEMPTS) {
            clearOtp(normalizedEmail);
        } else {
            attemptsCache.put(normalizedEmail, updatedAttempts);
        }

        return false;
    }

    public void clearOtp(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);

        Cache otpCache = getCache(OTP_CACHE);
        Cache attemptsCache = getCache(ATTEMPTS_CACHE);

        otpCache.evict(normalizedEmail);
        attemptsCache.evict(normalizedEmail);
    }

    private Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            throw new IllegalStateException(
                    "Cache is not configured: " + cacheName
            );
        }

        return cache;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
