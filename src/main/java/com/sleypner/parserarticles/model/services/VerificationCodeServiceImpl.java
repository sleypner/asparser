package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.VerificationCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<VerificationCode> findByEmail(String email) {
        TypedQuery<VerificationCode> query = em.createQuery(
                "SELECT v FROM VerificationCode v " +
                        "WHERE v.email = :email", VerificationCode.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    @Override
    public void delete(VerificationCode verificationCode) {
        em.remove(verificationCode);
    }

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        return em.merge(verificationCode);
    }
}
