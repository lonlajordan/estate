package com.estate.domain.entity;

import com.estate.domain.enumaration.Operator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Merchant extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "champs requis")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "uniquement alphanumérique")
    @Column(nullable = false)
    private String code = "";
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Operator operator;
    @NotBlank(message = "champs requis")
    @Pattern(regexp = "\\d+", message = "uniquement des chiffres")
    @Column(nullable = false)
    private String phone = "";
    @Column(nullable = false)
    private Boolean enabled = true;
}
