package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Users;
import com.sleypner.parserarticles.model.source.entityes.VerificationCode;
import com.sleypner.parserarticles.special.Special;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
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

    @Transactional(readOnly = true)
    @Override
    public List<Users> getAll() {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs ", Users.class);
        return query.getResultList();
    }

    @Override
    public Users save(Users users) {
        return entityManager.merge(users);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Users> getById(int id) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.id = :id", Users.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    @Transactional(readOnly = true)
    @Override
    public Users getByExternalId(String externalId) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.externalId = :externalId", Users.class);
        query.setParameter("externalId", externalId);
        query.setMaxResults(1);
        List<Users> usersList = query.getResultList();
        if (usersList.isEmpty()) {
            return null;
        }
        return usersList.getFirst();
    }

    @Override
    public Optional<Users> getByUsername(String username) {
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
    public boolean verifyEmailCode(String email, int code) {
        VerificationCode verificationCode = verificationCodeService.findByEmail(email).orElse(null);
        if (verificationCode != null && verificationCode.getVerificationCode() == code && !verificationCode.isExpired()) {

            Users user = this.getByEmail(email).orElse(null);
            if (user != null) {
                user.setEnabled(true);
                this.save(user);
                verificationCodeService.delete(verificationCode);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean sendVerificationCode(String email) {

        Optional<VerificationCode> dbVerificationCode = verificationCodeService.findByEmail(email);
        int code = generateRandomCode();

        if (dbVerificationCode.isPresent()) {

            LocalDateTime expiryDate = dbVerificationCode.get().getExpiryDate();
            LocalDateTime now = LocalDateTime.now().withNano(0);
            LocalDateTime updatedDate = dbVerificationCode.get().getUpdatedDate();

            long duration = Duration.between(updatedDate, now).toMinutes();

            if (expiryDate.isAfter(now) && duration > 1) {

                verificationCodeService.save(dbVerificationCode.get()
                        .setVerificationCode(code)
                        .setExpiryDate(now.plusMinutes(15)));

                return (emailService.sendCode(email, code));
            }
            return false;
        }

        verificationCodeService.save(VerificationCode.builder()
                .email(email)
                .verificationCode(code)
                .build());

        return (emailService.sendCode(email, code));
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public int generateRandomCode() {
        return Special.randomInt(100000, 999999);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Users> getByEmail(String email) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.email = :email", Users.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Users> search(String search) {
        TypedQuery<Users> query = entityManager.createQuery(
                "SELECT u FROM Users u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.email LIKE CONCAT('%',:search,'%') OR u.name LIKE CONCAT('%',:search,'%') OR u.username LIKE CONCAT('%',:search,'%')", Users.class);
        query.setParameter("search", search);
        List<Users> usersList = query.getResultList();
        if (usersList.isEmpty()) {
            return null;
        }
        return usersList;
    }

    @Override
    public void delete(int id) {
        entityManager.remove(entityManager.find(Users.class, id));
    }

    @Override
    public Users update(Users user) {
        return entityManager.merge(user);
    }
}
