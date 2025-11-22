package com.example.projectflow.service;

import com.example.projectflow.model.User;
import com.example.projectflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(String login, String rawPassword) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new RuntimeException("Пользователь с логином " + login + " уже существует");
        }
        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(rawPassword);

        return userRepository.save(user);
    }

    public Optional<User> authenticate(String login, String rawPassword) {
        return userRepository.findByLogin(login)
            .filter(user -> user.getPasswordHash().equals(rawPassword));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}