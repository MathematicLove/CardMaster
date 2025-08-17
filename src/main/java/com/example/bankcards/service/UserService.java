package com.example.bankcards.service;

import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User createUser(CreateUserRequest req, boolean allowRoles) {
        if (userRepository.existsByUsernameIgnoreCase(req.getUsername())) {
            throw new BadRequestException("Пользователь с таким логином уже существует");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setFullName(req.getFullName());
        if (allowRoles && req.getRoles() != null && !req.getRoles().isEmpty()) {
            u.setRoles(req.getRoles());
        } else {
            u.setRoles(Set.of(Role.USER));
        }
        return userRepository.save(u);
    }

    public Page<User> list(Pageable pageable) { return userRepository.findAll(pageable); }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) throw new NotFoundException("Пользователь не найден");
        userRepository.deleteById(id);
    }
}
