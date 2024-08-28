package com.estate.domain.service.impl;

import com.estate.domain.service.face.LeaseService;
import com.estate.repository.LeaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {
    private final LeaseRepository leaseRepository;
}
