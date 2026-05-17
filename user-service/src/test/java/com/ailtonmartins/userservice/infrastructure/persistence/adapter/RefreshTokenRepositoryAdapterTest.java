package com.ailtonmartins.userservice.infrastructure.persistence.adapter;

import com.ailtonmartins.userservice.domain.model.RefreshToken;
import com.ailtonmartins.userservice.infrastructure.persistence.entity.RefreshTokenEntity;
import com.ailtonmartins.userservice.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryAdapterTest {

    @Mock
    private JpaRefreshTokenRepository jpaRefreshTokenRepository;

    @InjectMocks
    private RefreshTokenRepositoryAdapter refreshTokenRepositoryAdapter;

    @Test
    void deveSalvarRefreshTokenUsandoJpaRepository() {
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(),
                "refresh-token",
                LocalDateTime.now().plusDays(7)
        );
        when(jpaRefreshTokenRepository.save(any(RefreshTokenEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken savedRefreshToken = refreshTokenRepositoryAdapter.save(refreshToken);

        ArgumentCaptor<RefreshTokenEntity> entityCaptor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(jpaRefreshTokenRepository).save(entityCaptor.capture());

        assertThat(entityCaptor.getValue().getToken()).isEqualTo("refresh-token");
        assertThat(savedRefreshToken.getId()).isEqualTo(refreshToken.getId());
    }

    @Test
    void deveBuscarRefreshTokenPorToken() {
        RefreshTokenEntity entity = new RefreshTokenEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "refresh-token",
                LocalDateTime.now().plusDays(7),
                false,
                LocalDateTime.now()
        );

        when(jpaRefreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(entity));

        Optional<RefreshToken> refreshToken = refreshTokenRepositoryAdapter.findByToken("refresh-token");

        assertThat(refreshToken).isPresent();
        assertThat(refreshToken.get().getToken()).isEqualTo("refresh-token");
    }

    @Test
    void deveRevogarRefreshTokensPorUsuario() {
        UUID userId = UUID.randomUUID();

        refreshTokenRepositoryAdapter.revokeByUserId(userId);

        verify(jpaRefreshTokenRepository).revokeByUserId(userId);
    }

    @Test
    void deveRemoverRefreshTokensExpirados() {
        refreshTokenRepositoryAdapter.deleteExpiredTokens();

        verify(jpaRefreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }
}
