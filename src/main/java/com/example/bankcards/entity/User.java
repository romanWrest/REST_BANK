package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.RoleUsers;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


// @Data - опасная аннотация на Entity. Дело в том, что она содержит в себе @ToString,
// и если где-нибудь ты выведешь в логи User, то ToString потянет за собой вложенные сущности (List<Card>)
// или твой пароль.
@Getter
@Setter
@RequiredArgsConstructor // Обычно @Data меняют на это трио

@ToString(exclude = {"password", "cards"}) // Можно исключать так
@Entity
@Table(name = "users")
@Valid // Это тут не нужно))
@Accessors(chain = true) // эта красивая анотация позволяет создавать билдер. С ним удобно заполнять сущности. Описание в UserService.class

// Очень рекомендую называть все классы по назначению. То есть, не просто User, а UserEntity.
// Тогда ты будешь сразу понимать, что за зверь перед тобой
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "password", length = 1000)
    private String password;

    //  @ToString.Exclude еще можно исключать так
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
