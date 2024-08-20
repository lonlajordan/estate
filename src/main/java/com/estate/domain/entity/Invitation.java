package com.estate.domain.entity;

import com.estate.domain.enumaration.ParticipationType;
import com.estate.domain.enumaration.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_game_sender_receiver", columnNames = {"game_id", "sender_id", "receiver_id"})})
public class Invitation extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Game game;
    @ManyToOne(optional = false)
    private Player sender;
    @ManyToOne(optional = false)
    private Player receiver;
    @Enumerated
    private ParticipationType type = ParticipationType.MEMBER;
    private LocalDateTime date = LocalDateTime.now();
    @Enumerated
    private Status status = Status.PENDING;
}
