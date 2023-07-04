package com.skab.javers.service;

import com.skab.javers.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public User create(User user);

    public User update(User user);

    public User findById(Long id);

    public List<User> getUsers();

    Optional<User> findByUsername(String username);
}
