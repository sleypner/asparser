package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fortress_history")
public class FortressHistory implements Comparable<FortressHistory> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "fortress_id")
    private int fortressId;
    @Column(name = "clan_id")
    private int clanId;
    @Column(name = "coffer")
    private long coffer;
    @Column(name = "hold_time")
    private int holdTime;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public FortressHistory(long coffer, int holdTime) {
        this.createdDate = LocalDateTime.now().withNano(0);
        this.coffer = coffer;
        this.holdTime = holdTime;
        this.updatedDate = LocalDateTime.now().withNano(0);
    }

    public FortressHistory(int fortressId, int clanId, long coffer, int holdTime) {
        this.createdDate = LocalDateTime.now().withNano(0);
        this.fortressId = fortressId;
        this.clanId = clanId;
        this.coffer = coffer;
        this.holdTime = holdTime;
        this.updatedDate = LocalDateTime.now().withNano(0);
    }

    @Override
    public int compareTo(FortressHistory o) {
        return Integer.compare(getId(),o.id);
    }
}


