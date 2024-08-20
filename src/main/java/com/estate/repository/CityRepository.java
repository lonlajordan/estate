package com.estate.repository;

import com.estate.domain.entity.Housing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CityRepository extends CrudRepository<Housing, Long>, JpaRepository<Housing, Long> {
    Housing findByName(String name);
    Housing findFirstByIdIn(List<Long> ids);
    List<Housing> findAllByOrderByNameAsc();
    List<Housing> findAllByStandingIdOrderByNameAsc(Long id);
}
