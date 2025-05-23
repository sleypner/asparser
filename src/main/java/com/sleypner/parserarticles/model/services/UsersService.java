package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Users;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    List<Users> getAll();

    Users save(Users users);

    Users getById(int id);

    Users getByExternalId(String externalId);

    Users getByUsername(String username);
    Optional<Users> getOptionalByUsername(String username);

    boolean verifyEmailCode(String email, int code);

    void resendVerificationCode(String email);

    int generateRandomCode();

    Users findByEmail(String email);

    List<Users> search(String search);

    void delete(int id);

    Users update(Users user);
}
