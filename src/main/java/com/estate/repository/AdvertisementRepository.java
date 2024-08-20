package com.estate.repository;

import com.estate.domain.entity.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface AdvertisementRepository extends CrudRepository<Advertisement, Long>, JpaRepository<Advertisement, Long> {
    List<Advertisement> findAllByOrderByCreatedAtDesc();
    Page<Advertisement> findAllByOrderByViewsAsc(Pageable pageable);
    List<Advertisement> findAllByIdIn(Set<Long> ids);
}
