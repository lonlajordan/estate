package com.estate.repository;

import com.estate.domain.entity.Housing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HousingRepository extends JpaRepository<Housing, Long> {
    List<Housing> findAllByOrderByNameAsc();
}
