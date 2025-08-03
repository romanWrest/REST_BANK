package com.example.bankcards.repository;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.RoleUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
