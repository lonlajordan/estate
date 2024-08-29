package com.estate.domain.service.face;

import com.estate.domain.entity.Lease;
import com.estate.domain.entity.Notification;
import com.estate.domain.form.LeaseSearch;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.Optional;

public interface LeaseService {
    Page<Lease> findAll(int page);

    Page<Lease> findAll(LeaseSearch form);

    Optional<Lease> findById(long id);

    long count();

    Notification toggleById(long id, Principal principal);
}
