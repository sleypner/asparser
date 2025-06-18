package dev.sleypner.asparser.service.core.auth.user;

import dev.sleypner.asparser.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    List<User> getAll();

    User save(User user);

    Optional<User> getById(int id);

    User getByExternalId(String externalId);

    Optional<User> getByUsername(String username);

    boolean verifyEmailCode(String email, int code);

    boolean sendVerificationCode(String email);

    int generateRandomCode();

    Optional<User> getByEmail(String email);

    List<User> search(String search);

    void delete(int id);

    User update(User user);
}
