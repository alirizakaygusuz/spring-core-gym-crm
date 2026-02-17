package com.alirizakaygusuz.gymcrm.monitoring.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppMetrics {

    private final MeterRegistry meterRegistry;

    // ===== LOGIN METRICS =====

    public void incrementLoginAttempts() {
        meterRegistry.counter("auth.login.attempts.total").increment();
    }

    public void incrementLoginSuccess() {
        meterRegistry.counter("auth.login.success.total").increment();
    }

    public void incrementLoginFailure() {
        meterRegistry.counter("auth.login.failures.total").increment();
    }

    // ===== USER(TRAINEE & TRAINER) REGISTRATION METRICS =====

    public void incrementTraineeRegistrationAttempts() {
        meterRegistry.counter("user.registration.attempts.total", "type", "trainee").increment();
    }

    public void incrementTraineeRegistrationSuccess() {
        meterRegistry.counter("user.registration.success.total", "type", "trainee").increment();
    }


    public void incrementTrainerRegistrationAttempts() {
        meterRegistry.counter("user.registration.attempts.total", "type", "trainer").increment();
    }

    public void incrementTrainerRegistrationSuccess() {
        meterRegistry.counter("user.registration.success.total", "type", "trainer").increment();
    }
}