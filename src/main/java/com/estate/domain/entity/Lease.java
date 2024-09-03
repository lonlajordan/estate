package com.estate.domain.entity;

import com.estate.domain.enumaration.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Lease extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    @OneToOne(optional = false)
    private Payment payment;
    @ManyToOne
    private Housing housing;
    @OneToOne
    private Lease nextLease;

    @Transient
    public String getBackground(){
        LocalDate date = getRealEndDate();
        if(date != null) {
            LocalDate today = LocalDate.now();
            if(date.isAfter(today)) {
                long n = ChronoUnit.DAYS.between(today, date);
                if(n < 30){
                    return "bg-danger";
                } else if(n < 60){
                    return "bg-warning";
                }
                return "bg-success";
            } else {
                return "bg-danger";
            }
        }
        return "";
    }

    public LocalDate getRealEndDate(){
        return nextLease != null && nextLease.getEndDate() != null && (endDate == null || endDate.isBefore(nextLease.getEndDate())) ? nextLease.getEndDate() : endDate;
    }
}
