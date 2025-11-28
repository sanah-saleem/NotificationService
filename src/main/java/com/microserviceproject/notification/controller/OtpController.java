package com.microserviceproject.notification.controller;

import com.microserviceproject.notification.model.OtpRequest;
import com.microserviceproject.notification.model.OtpVerificationResponse;
import com.microserviceproject.notification.model.OtpVerifyRequest;
import com.microserviceproject.notification.service.OtpService;
import jakarta.validation.Valid;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestOtp(@Valid @RequestBody OtpRequest request) {
        otpService.requestOtp(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("OTP generated and notification queued");
    }

    @PostMapping("/verify")
    public ResponseEntity<OtpVerificationResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        OtpVerificationResponse response = otpService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

}
