package com.example.bankcards.dto.user;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;

import java.time.Instant;
import java.util.Set;

public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private Set<Role> roles;
    private boolean enabled;
    private Instant createdAt;

    public static UserResponse from(User u) {
        UserResponse r = new UserResponse();
        r.id = u.getId();
        r.username = u.getUsername();
        r.fullName = u.getFullName();
        r.roles = u.getRoles();
        r.enabled = u.isEnabled();
        r.createdAt = u.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public Set<Role> getRoles() { return roles; }
    public boolean isEnabled() { return enabled; }
    public Instant getCreatedAt() { return createdAt; }
}