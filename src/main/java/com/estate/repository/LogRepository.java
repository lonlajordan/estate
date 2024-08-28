package com.estate.repository;

import com.estate.domain.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends JpaRepository<Log, Long>, JpaSpecificationExecutor<Log> {
    Page<Log> findAllByOrderByCreationDateDesc(Pageable pageable);
}
