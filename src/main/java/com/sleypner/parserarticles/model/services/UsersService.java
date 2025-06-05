package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Users;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    List<Users> getAll();

    Users save(Users users);

    Optional<Users> getById(int id);

    Users getByExternalId(String externalId);

    Optional<Users> getByUsername(String username);

    boolean verifyEmailCode(String email, int code);

    boolean sendVerificationCode(String email);

    int generateRandomCode();

    Optional<Users> getByEmail(String email);

    List<Users> search(String search);

    void delete(int id);

    Users update(Users user);
}
