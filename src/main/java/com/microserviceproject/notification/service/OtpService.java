package com.microserviceproject.notification.service;

import com.microserviceproject.notification.config.NotificationOtpProperties;
import com.microserviceproject.notification.exception.OtpRateLimitExceededException;
import com.microserviceproject.notification.model.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    private final StringRedisTemplate redisTemplate;
    private final NotificationDispatcher notificationDispatcher;
    private final NotificationOtpProperties otpProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(StringRedisTemplate redisTemplate,
                      NotificationDispatcher notificationDispatcher,
                      NotificationOtpProperties otpProperties) {
        this.redisTemplate = redisTemplate;
        this.notificationDispatcher = notificationDispatcher;
        this.otpProperties = otpProperties;
    }

    public void requestOtp(OtpRequest request) {
        NotificationChannelType channelType =
                request.channelType() != null ? request.channelType() : NotificationChannelType.EMAIL;
        // Rate limiting per day
        checkAndIncrementDailyLimit(request.purpose(), request.userId());
        //Generate and store OTP
        String otpCode = generateOtpCode(otpProperties.length());
        String key = buildRedisKey(request.purpose(), request.userId());
        Duration ttl = Duration.ofSeconds(otpProperties.ttlSeconds());

        Map<String, String> data = new HashMap<>();
        data.put("code", otpCode);
        data.put("channel", channelType.name());
        data.put("destination", request.email());
        data.put("attempts", "0");

        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, ttl);

        log.info("Stored OTP in Redis for userId={}, purpose={}, ttl={} seconds",
                request.userId(), request.purpose(), ttl.toSeconds());

        String subject = "Your OTP Code";
        String body = String.format("Your OTP for %s is: %s%nThis code is valid for %d minutes.",
                request.purpose(), otpCode, ttl.toMinutes());

        NotificationMessage message = new NotificationMessage(
                channelType, request.email(), subject,
                body, null // metadata optional
        );
        notificationDispatcher.dispatch(message);
    }

    public OtpVerificationResponse verifyOtp(OtpVerifyRequest request) {
        String key = buildRedisKey(request.purpose(), request.userId());
        Map<Object,Object> entries = redisTemplate.opsForHash().entries(key);

        if (entries == null || entries.isEmpty()) {
            log.info("No OTP found in Redis for userId={}, purpose={}", request.userId(), request.purpose());
            return new OtpVerificationResponse( OtpVerificationStatus.EXPIRED_OR_NOT_FOUND, "OTP expired or not found" );
        }

        String storedCode = (String) entries.get("code");
        String attemptsStr = (String) entries.getOrDefault("attempts", "0");
        int attempts = Integer.parseInt(attemptsStr);
        int maxAttempts = otpProperties.maxAttempts();

        if (attempts >= maxAttempts) {
            redisTemplate.delete(key);
            log.info("OTP attempts exceeded for userId={}, purpose={}", request.userId(), request.purpose());
            return new OtpVerificationResponse( OtpVerificationStatus.TOO_MANY_ATTEMPTS, "Too many invalid attempts. OTP blocked." );
        }

        if(!storedCode.equals(request.otp())) {
            attempts++;
            redisTemplate.opsForHash().put(key, "attempts", String.valueOf(attempts));
            log.info("Invalid OTP for userId={}, purpose={}, attempts={}", request.userId(), request.purpose(), attempts);
            if (attempts >= maxAttempts) {
                redisTemplate.delete(key);
                return new OtpVerificationResponse( OtpVerificationStatus.TOO_MANY_ATTEMPTS, "Too many invalid attempts. OTP blocked." );
            }
            return new OtpVerificationResponse( OtpVerificationStatus.INVALID, "Invalid OTP" );
        }
        // otp matches
        redisTemplate.delete(key);
        log.info("OTP verified successfully for userId={}, purpose={}", request.userId(), request.purpose());
        return new OtpVerificationResponse( OtpVerificationStatus.VALID, "OTP verified successfully" );
    }

    private String buildRedisKey(String purpose, String userId) {
        return "otp:" + purpose + ":" + userId;
    }

    private String buildDailyLimitKey(String purpose, String userId) {return "otp:count:" + purpose + ":" + userId;}

    private String generateOtpCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = secureRandom.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    private void checkAndIncrementDailyLimit(String purpose, String userId) {
        String key = buildDailyLimitKey(purpose, userId);
        Long current = redisTemplate.opsForValue().increment(key);
        if (current != null && current == 1L) {
            redisTemplate.expire(key, Duration.ofHours(24));
        }
        int maxPerDay = otpProperties.maxPerDay();
        if (current != null && current > maxPerDay) {
            log.warn("OTP daily limit exceeded for userId={}, purpose={}, count={}", userId, purpose, current);
            throw new OtpRateLimitExceededException(
                    "OTP request limit exceeded for today. Please try again later.");
        }
    }

}
