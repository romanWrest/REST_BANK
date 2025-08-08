package com.example.bankcards;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.RoleUsers;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class LiquibaseMigrationTest {
    @Autowired
    private UserRepository userRepository;


    @Test
    void testSaveEntity() {
        userRepository.save(new UserEntity().setRole(RoleUsers.ROLE_USER).setEmail("test@test.com"));
        assertEquals(1, userRepository.findAll().size());
    }


}