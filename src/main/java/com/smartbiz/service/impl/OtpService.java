package com.smartbiz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final CacheManager cacheManager;
    private static final String OTP_CACHE = "otpCache";
    private static final SecureRandom random = new SecureRandom();

    public String generateAndStoreOtp(String email) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(999999));
        Cache cache = cacheManager.getCache(OTP_CACHE);
        if (cache != null) {
            cache.put(email.toLowerCase(), otp);
        }
        log.info("OTP generated for {}: {} (expires in 5 min)", email, otp);
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        Cache cache = cacheManager.getCache(OTP_CACHE);
        if (cache == null) return false;
        String stored = cache.get(email.toLowerCase(), String.class);
        return otp != null && otp.equals(stored);
    }

    public void clearOtp(String email) {
        Cache cache = cacheManager.getCache(OTP_CACHE);
        if (cache != null) {
            cache.evict(email.toLowerCase());
        }
    }
}
