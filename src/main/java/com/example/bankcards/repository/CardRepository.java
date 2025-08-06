package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {
    List<CardEntity> findByUserId(Long userId);

    Optional<CardEntity> findByNumber(String number);

    Page<CardEntity> findAll(Pageable pageable);

    List<CardEntity> findByStatusNotAndExpiryDateBefore(CardStatus status, LocalDate date);

    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CardEntity> findById(Long id);

    Page<CardEntity> findByUserId(Long userId, Pageable pageable);

}
