package com.estate.repository;

import com.estate.domain.entity.Standing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandingRepository extends JpaRepository<Standing, String>, CrudRepository<Standing, String> {
    List<Standing> findAllByOrderByNameAsc();
    List<Standing> findAllByActiveTrueOrderByNameAsc();
    List<Standing> findAllByActiveTrueOrderByRentAsc();
}
