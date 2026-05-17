package com.ailtonmartins.userservice.infrastructure.persistence.repository;

import com.ailtonmartins.userservice.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("update RefreshTokenEntity token set token.revoked = true where token.userId = :userId")
    void revokeByUserId(UUID userId);

    @Modifying
    @Query("delete from RefreshTokenEntity token where token.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
