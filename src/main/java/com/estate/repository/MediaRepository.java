package com.estate.repository;

import com.estate.domain.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface MediaRepository extends CrudRepository<Media, Long>, JpaRepository<Media, Long> {
    List<Media> findAllByOrderByCreatedAtDesc();
    Page<Media> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Media> findAllByIdIn(Set<Long> ids);
}
