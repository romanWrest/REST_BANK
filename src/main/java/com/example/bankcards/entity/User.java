package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.RoleUsers;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "users")
@Valid
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password", length = 1000)
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();
    @Column(name = "phone_number", nullable = false)
    @Size(min = 16, max = 255)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleUsers role;


    public boolean isAdmin() {
        return role == RoleUsers.ROLE_ADMIN;
    }


}
