package com.estate.repository;


import com.estate.domain.entity.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository <Visitor, Long>, JpaSpecificationExecutor<Visitor> {
    Optional<Visitor> findByEmail(String email);
    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteAllByCreationDateBefore(LocalDateTime date);
    List<Visitor> findVisitorByCreationDateEquals(LocalDateTime localDateTime);

    Page<Visitor> findAllByOrderByCreationDateDesc(Pageable pageable);
}
