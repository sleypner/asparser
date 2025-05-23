package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Users;
import com.sleypner.parserarticles.model.source.entityes.VerificationCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Repository
public class UsersServiceImpl implements UsersService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final EmailServiceImpl emailService;
    private final VerificationCodeService verificationCodeService;

    @Autowired
    public UsersServiceImpl(EntityManager entityManager, EmailServiceImpl emailService, VerificationCodeService verificationCodeService) {
        this.entityManager = entityManager;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
    }

    @Override
    public List<Users> getAll() {
        TypedQuery<Users> query = entityManager.createQuery("FROM Users users", Users.class);
        return query.getResultList();
    }

    @Transactional
    @Override
    public Users save(Users users) {
        return entityManager.merge(users);
    }

    @Override
    public Users getById(int id) {
        return entityManager.find(Users.class, id);
    }

    @Override
    public Users getByExternalId(String externalId) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "JOIN FETCH u.roles " +
                        "WHERE u.externalId = :externalId", Users.class);
        query.setParameter("externalId", externalId);
        query.setMaxResults(1);
        List<Users> usersList = query.getResultList();
        if (usersList.isEmpty()) {
            return null;
        }
        return usersList.getFirst();
    }
    @Transactional
    @Override
    @Cacheable("users")
    public Optional<Users> getOptionalByUsername(String username) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.username = :username", Users.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }
    @Transactional
    @Override
    public Users getByUsername(String username) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.username = :username", Users.class);
        query.setParameter("username", username);
        query.setMaxResults(1);
        List<Users> usersList = query.getResultList();
        if (usersList.isEmpty()) {
            return null;
        }
        return usersList.getFirst();
    }

    @Transactional
    @Override
    public boolean verifyEmailCode(String email, int code) {
        VerificationCode verificationCode = verificationCodeService.findByEmail(email);

        if (verificationCode != null && verificationCode.getVerificationCode() == code && !verificationCode.isExpired()) {

            Users user = this.findByEmail(email);
            user.setEnabled(true);
            this.save(user);
            verificationCodeService.delete(verificationCode);

            return true;
        }

        return false;
    }

    @Transactional
    @Override
    public void resendVerificationCode(String email) {
        int newCode = generateRandomCode();
        VerificationCode verificationCode = verificationCodeService.findByEmail(email);

        if (verificationCode == null) {
            verificationCode = new VerificationCode(email, newCode);
        } else {
            verificationCode.setVerificationCode(newCode);
            verificationCode.setUpdatedDate(LocalDateTime.now());
            verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(1));
        }

        verificationCodeService.save(verificationCode);
        emailService.sendVerificationCode(email, newCode);
    }

    @Override
    public int generateRandomCode() {
        return new Random().nextInt(999999);
    }

    @Override
    public Users findByEmail(String email) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "JOIN FETCH u.roles " +
                        "WHERE u.email = :email", Users.class);
        query.setParameter("email", email);
        query.setMaxResults(1);
        List<Users> usersList = query.getResultList();
        if (usersList.isEmpty()) {
            return null;
        }
        return usersList.getFirst();
    }

    @Override
    public List<Users> search(String search) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "JOIN FETCH u.roles " +
                        "WHERE u.email LIKE CONCAT('%',:search,'%') OR u.name LIKE CONCAT('%',:search,'%') OR u.username LIKE CONCAT('%',:search,'%')", Users.class);
        query.setParameter("search", search);
        List<Users> usersList = query.getResultList();
        if (usersList.isEmpty()) {
            return null;
        }
        return usersList;
    }

    @Transactional
    @Override
    public void delete(int id) {
        entityManager.remove(entityManager.find(Users.class, id));
    }

    @Transactional
    @Override
    public Users update(Users user) {
        return entityManager.merge(user);
    }
}
