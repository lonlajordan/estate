package com.estate.domain.service.face;

import com.estate.domain.entity.Lease;
import com.estate.domain.entity.Notification;
import com.estate.domain.form.LeaseSearch;
import com.estate.domain.form.MutationForm;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Optional;

public interface LeaseService {
    Page<Lease> findAll(int page);

    Page<Lease> findAll(LeaseSearch form);

    Optional<Lease> findById(long id);

    ResponseEntity<?> download(long id);

    long count();

    Notification activate(long id, Long housingId, Model model);

    Notification mutate(MutationForm mutation, Principal principal);
}
