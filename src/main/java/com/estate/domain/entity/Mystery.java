package com.estate.domain.entity;

import com.estate.domain.enumaration.QuestionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Mystery extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String week;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String frFormulation;
    @Column(columnDefinition = "TEXT")
    private String frPropositionA;
    @Column(columnDefinition = "TEXT")
    private String frPropositionB;
    @Column(columnDefinition = "TEXT")
    private String frPropositionC;
    @Column(columnDefinition = "TEXT")
    private String frPropositionD;
    @Column(nullable = false)
    private String frAnswer;

    @Column(columnDefinition = "TEXT")
    private String enFormulation;
    @Column(columnDefinition = "TEXT")
    private String enPropositionA;
    @Column(columnDefinition = "TEXT")
    private String enPropositionB;
    @Column(columnDefinition = "TEXT")
    private String enPropositionC;
    @Column(columnDefinition = "TEXT")
    private String enPropositionD;
    private String enAnswer;

    @Column(columnDefinition = "TEXT")
    private String esFormulation;
    @Column(columnDefinition = "TEXT")
    private String esPropositionA;
    @Column(columnDefinition = "TEXT")
    private String esPropositionB;
    @Column(columnDefinition = "TEXT")
    private String esPropositionC;
    @Column(columnDefinition = "TEXT")
    private String esPropositionD;
    private String esAnswer;

    @Column(columnDefinition = "TEXT")
    private String ptFormulation;
    @Column(columnDefinition = "TEXT")
    private String ptPropositionA;
    @Column(columnDefinition = "TEXT")
    private String ptPropositionB;
    @Column(columnDefinition = "TEXT")
    private String ptPropositionC;
    @Column(columnDefinition = "TEXT")
    private String ptPropositionD;
    private String ptAnswer = "";

    @Column(columnDefinition = "TEXT")
    private String arFormulation;
    @Column(columnDefinition = "TEXT")
    private String arPropositionA;
    @Column(columnDefinition = "TEXT")
    private String arPropositionB;
    @Column(columnDefinition = "TEXT")
    private String arPropositionC;
    @Column(columnDefinition = "TEXT")
    private String arPropositionD;
    private String arAnswer;

    private boolean deliberated = false;
    private boolean recompensed = false;
    private boolean physical = false;
    @Enumerated
    private QuestionType type = QuestionType.QCM;
    private Boolean deleted = Boolean.FALSE;

}
