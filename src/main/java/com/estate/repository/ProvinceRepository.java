package com.estate.repository;

import com.estate.domain.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProvinceRepository extends CrudRepository<Province, Long>, JpaRepository<Province, Long> {
    Province findByName(String name);
    List<Province> findAllByOrderByNameAsc();
}
