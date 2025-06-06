package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.VerificationCode;

import java.util.Optional;

public interface VerificationCodeService {
    Optional<VerificationCode> findByEmail(String email);

    void delete(VerificationCode verificationCode);

    VerificationCode save(VerificationCode verificationCode);

}
