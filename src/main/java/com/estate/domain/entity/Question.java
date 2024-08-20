package com.estate.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Question extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answer = "";
    private int duration = 60; // in seconds
    private Boolean uploaded = Boolean.FALSE;
    private Boolean deleted = Boolean.FALSE;
    private String formulationFileName;
    private Long formulationSize = 0L;
    private Long justificationSize = 0L;
    private String justificationFileName;
    private String formulationLink;
    private String justificationLink;

    @ManyToMany(cascade = CascadeType.DETACH, mappedBy = "questions")
    private List<Game> games = new ArrayList<>();

    public static String readableSize(Long size) {
        if(size == null || size <= 0) return "0 Ko";
        final String[] units = new String[] { "o", "Ko", "Mo", "Go", "To" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Transient
    public String getSize(){
        return readableSize(formulationSize);
    }

    @Transient
    public String getAnswerSize(){
        return readableSize(justificationSize);
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.answer != null) this.answer = this.answer.trim().toUpperCase();
    }
}
