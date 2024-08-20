package com.estate.repository;

import com.estate.domain.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    boolean existsByCode(String code);
    List<Setting> findAllByOrderById();
    Optional<Setting> findByCode(String code);
}
