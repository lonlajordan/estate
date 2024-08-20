package com.estate.repository;

import com.estate.domain.enumaration.Operator;
import com.estate.domain.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends CrudRepository<Merchant, Long>, JpaRepository<Merchant, Long> {
    Optional<Merchant> findByOperatorAndEnabledTrue(Operator operator);
}
