package com.ailtonmartins.userservice.infrastructure.persistence.adapter;

import com.ailtonmartins.userservice.domain.model.RefreshToken;
import com.ailtonmartins.userservice.domain.repository.RefreshTokenRepository;
import com.ailtonmartins.userservice.infrastructure.persistence.mapper.RefreshTokenPersistenceMapper;
import com.ailtonmartins.userservice.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    public RefreshTokenRepositoryAdapter(JpaRefreshTokenRepository jpaRefreshTokenRepository) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return RefreshTokenPersistenceMapper.toDomain(
                jpaRefreshTokenRepository.save(RefreshTokenPersistenceMapper.toEntity(refreshToken))
        );
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token)
                .map(RefreshTokenPersistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public void revokeByUserId(UUID userId) {
        jpaRefreshTokenRepository.revokeByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        jpaRefreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
