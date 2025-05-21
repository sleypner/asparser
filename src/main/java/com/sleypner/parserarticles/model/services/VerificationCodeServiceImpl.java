package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.VerificationCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public VerificationCode findByEmail(String email) {
        TypedQuery<VerificationCode> query = em.createQuery(
                "SELECT v FROM VerificationCode v " +
                        "WHERE v.email = :email", VerificationCode.class);
        query.setParameter("email", email);
        query.setMaxResults(1);
        List<VerificationCode> clanList = query.getResultList();
        if (clanList.isEmpty()) {
            return null;
        }
        return clanList.getFirst();
    }

    @Transactional
    @Override
    public void delete(VerificationCode verificationCode) {
        em.remove(verificationCode);
    }

    @Transactional
    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        return em.merge(verificationCode);
    }
}
