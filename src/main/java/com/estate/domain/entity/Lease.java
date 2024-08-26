package com.estate.domain.entity;

import com.estate.domain.enumaration.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Lease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToOne
    private Payment payment;
    @ManyToOne
    private Housing housing;
    @OneToOne
    private Lease nextLease;
}
