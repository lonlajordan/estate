package com.estate.domain.entity;

import com.estate.domain.form.PartnerForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Partner extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToOne
    private Picture logo;


    public Partner toPartner(PartnerForm partnerForm){
        Partner partner = new Partner();
        partner.setName(partnerForm.getName());

        return partner;
    }
}
