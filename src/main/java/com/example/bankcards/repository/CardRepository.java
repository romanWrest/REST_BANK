package com.example.bankcards.repository;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Banks;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {
    List<CardEntity> findByUserId(Long userId);
    Optional<CardEntity> findByNumber(String number);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CardEntity> findById(Long id);

    Page<CardEntity> findByUserId(Long userId, Pageable pageable);

}
