package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.VerificationCode;

public interface VerificationCodeService {
    VerificationCode findByEmail(String email);

    void delete(VerificationCode verificationCode);

    VerificationCode save(VerificationCode verificationCode);

}
