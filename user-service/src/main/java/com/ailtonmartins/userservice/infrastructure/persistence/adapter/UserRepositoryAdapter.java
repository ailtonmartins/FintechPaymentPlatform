package com.ailtonmartins.userservice.infrastructure.persistence.adapter;

import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.domain.repository.UserRepository;
import com.ailtonmartins.userservice.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.ailtonmartins.userservice.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        return UserPersistenceMapper.toDomain(
                jpaUserRepository.save(UserPersistenceMapper.toEntity(user))
        );
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
}
