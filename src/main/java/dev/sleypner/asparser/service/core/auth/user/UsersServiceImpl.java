package dev.sleypner.asparser.service.core.auth.user;

import dev.sleypner.asparser.domain.model.User;
import dev.sleypner.asparser.domain.model.VerificationCode;
import dev.sleypner.asparser.service.core.auth.email.EmailServiceImpl;
import dev.sleypner.asparser.service.core.auth.verification.VerificationCodeService;
import dev.sleypner.asparser.util.Util;
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
    public List<User> getAll() {
        List<User> userList = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs ", User.class);
        return query.getResultList();
    }

    @Override
    public User save(User user) {
        return entityManager.merge(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getById(int id) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    @Transactional(readOnly = true)
    @Override
    public User getByExternalId(String externalId) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.externalId = :externalId", User.class);
        query.setParameter("externalId", externalId);
        query.setMaxResults(1);
        List<User> userList = query.getResultList();
        if (userList.isEmpty()) {
            return null;
        }
        return userList.getFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }

    @Transactional
    @Override
    public boolean verifyEmailCode(String email, int code) {
        VerificationCode verificationCode = verificationCodeService.findByEmail(email).orElse(null);
        if (verificationCode != null && verificationCode.getVerificationCode() == code && !verificationCode.isExpired()) {

            User user = this.getByEmail(email).orElse(null);
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
        return Util.randomInt(100000, 999999);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> search(String search) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.roles " +
                        "LEFT JOIN FETCH u.userActionLogs " +
                        "WHERE u.email LIKE CONCAT('%',:search,'%') OR u.name LIKE CONCAT('%',:search,'%') OR u.username LIKE CONCAT('%',:search,'%')", User.class);
        query.setParameter("search", search);
        List<User> userList = query.getResultList();
        if (userList.isEmpty()) {
            return null;
        }
        return userList;
    }

    @Override
    public void delete(int id) {
        entityManager.remove(entityManager.find(User.class, id));
    }

    @Override
    public User update(User user) {
        return entityManager.merge(user);
    }
}
