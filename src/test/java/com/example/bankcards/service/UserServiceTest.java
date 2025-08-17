package com.example.bankcards.service;

import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks CreateUserRequest userService;

    @Test
    void create_assigns_default_role_user_and_encodes_password() {
        CreateUserRequest req = new CreateUserRequest();
        ReflectionTestUtils.setField(req, "username", "neo");
        ReflectionTestUtils.setField(req, "password", "rawpwd");
        ReflectionTestUtils.setField(req, "fullName", "Neo Anderson");

        given(passwordEncoder.encode("rawpwd")).willReturn("ENC");
        given(userRepository.save(any(User.class))).willAnswer(inv -> {
            User u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 1L);
            return u;
        });


        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getUsername()).isEqualTo("neo");
        assertThat(saved.getPassword()).isEqualTo("ENC");
        assertThat(saved.getRoles()).extracting(Object::toString).contains("USER");
    }
}
