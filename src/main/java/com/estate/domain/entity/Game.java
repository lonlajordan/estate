package com.estate.domain.entity;

import com.estate.domain.enumaration.GameType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Game extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date = LocalDateTime.now();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ending;
    @Enumerated
    private GameType type = GameType.QUIZ;
    @ManyToOne
    private Player initiator;
    private int tombola; // number of participants tirage au sort
    @Column(nullable = false)
    private int duration = 30; // duration of the game in minutes
    private Boolean closed = Boolean.FALSE; // define if game is closed
    @ManyToOne
    private Player winner;
    @OneToMany(mappedBy = "game", orphanRemoval = true)
    private List<Invitation> invitations = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.DETACH)
    @OrderBy("id asc")
    private List<Question> questions = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.DETACH)
    @OrderBy("id asc")
    private List<Advertisement> advertisements = new ArrayList<>();

    public boolean getEditable(){
        return LocalDateTime.now().isBefore(date);
    }

    public Boolean getPlayed(){
        return LocalDateTime.now().isAfter(ending);
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        this.ending = this.date.plusMinutes(this.duration);
    }
}
