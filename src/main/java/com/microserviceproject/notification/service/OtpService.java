package com.microserviceproject.notification.service;

import com.microserviceproject.notification.integration.EmailNotificationProducer;
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

    private static final int OTP_LENGTH = 6;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final int MAX_ATTEMPTS = 5;

    private final StringRedisTemplate redisTemplate;
    private final EmailNotificationProducer emailNotificationProducer;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(StringRedisTemplate redisTemplate,
                      EmailNotificationProducer emailNotificationProducer) {
        this.redisTemplate = redisTemplate;
        this.emailNotificationProducer = emailNotificationProducer;
    }

    public void requestOtp(OtpRequest request) {
        String otpCode = generateOtpCode();
        String key = buildRedisKey(request.purpose(), request.userId());

        Map<String, String> data = new HashMap<>();
        data.put("code", otpCode);
        data.put("channel", "EMAIL");
        data.put("destination", request.email());
        data.put("attempts", "0");

        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, OTP_TTL);

        log.info("Stored OTP in Redis for userId={}, purpose={}, ttl={} seconds",
                request.userId(), request.purpose(), OTP_TTL.toSeconds());

        String subject = "Your OTP Code";
        String body = String.format("Your OTP for %s is: %s%nThis code is valid for %d minutes.",
                request.purpose(), otpCode, OTP_TTL.toMinutes());

        EmailRequest emailRequest = new EmailRequest(request.email(), subject, body);
        emailNotificationProducer.sendEmailNotification(emailRequest);
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

        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(key);
            log.info("OTP attempts exceeded for userId={}, purpose={}", request.userId(), request.purpose());
            return new OtpVerificationResponse( OtpVerificationStatus.TOO_MANY_ATTEMPTS, "Too many invalid attempts. OTP blocked." );
        }

        if(!storedCode.equals(request.otp())) {
            attempts++;
            redisTemplate.opsForHash().put(key, "attempts", String.valueOf(attempts));
            log.info("Invalid OTP for userId={}, purpose={}, attempts={}", request.userId(), request.purpose(), attempts);
            if (attempts >= MAX_ATTEMPTS) {
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

    private String generateOtpCode() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int digit = secureRandom.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

}
