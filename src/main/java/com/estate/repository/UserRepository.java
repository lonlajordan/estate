package com.estate.repository;


import com.estate.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long>, JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token);
    List<User> findAllByDeletedFalseOrderByLastLoginDesc();
    long countAllByDeletedFalse();
}