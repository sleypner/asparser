package dev.sleypner.asparser.service.core.auth.verification;

import dev.sleypner.asparser.domain.model.VerificationCode;

import java.util.Optional;

public interface VerificationCodeService {
    Optional<VerificationCode> findByEmail(String email);

    void delete(VerificationCode verificationCode);

    VerificationCode save(VerificationCode verificationCode);

}
