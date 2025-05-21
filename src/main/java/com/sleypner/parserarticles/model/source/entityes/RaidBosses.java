package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "raid_bosses")
public class RaidBosses implements Comparable<RaidBosses> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "type")
    String type;
    @Column(name = "server")
    String server;
    @Column(name = "date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime date;
    @Column(name = "respawnStart", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime respawnStart;
    @Column(name = "respawnEnd", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime respawnEnd;
    @Column(name = "count_killing")
    int countKilling;
    @Column(name = "last_killer")
    String lastKiller;
    @Column(name = "last_killers_clan")
    String lastKillersClan;
    @Column(name = "attackers_count")
    int attackersCount;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public RaidBosses(String name, String type, String server, LocalDateTime date) {
        this.name = name;
        this.type = type;
        this.server = server;
        this.date = date;
        if(date != null) {
            SetRespawnBoss();
        }
        this.createdDate = LocalDateTime.now().withNano(0);

    }

    public RaidBosses(String name, String type, String server, LocalDateTime date, String lastKiller, String lastKillersClan, int attackersCount) {
        this.name = name;
        this.type = type;
        this.server = server;
        this.date = date;
        if(date != null) {
            SetRespawnBoss();
        }
        this.lastKiller = lastKiller;
        this.lastKillersClan = lastKillersClan;
        this.attackersCount = attackersCount;
        this.createdDate = LocalDateTime.now().withNano(0);
    }


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
        if(date != null) {
            SetRespawnBoss();
        }
    }

    @Override
    public int compareTo(RaidBosses o) {
        return getDate().compareTo(o.getDate());
    }

    private void SetRespawnBoss(){
        String bossType = getType();
        String bossName = getName();
        if(Objects.equals(bossType, "Key Bosses")){
            this.respawnStart = getDate().plusHours(18);
            this.respawnEnd = getDate().plusHours(30);
        }
        else if(Objects.equals(bossType, "Epic Bosses")){
            switch (bossName){
                case "Core":
                    this.respawnStart = getDate().plusHours(20);
                    this.respawnEnd = getDate().plusHours(28);
                    break;
                case "Beleth":
                    this.respawnStart = getDate().plusDays(6).withHour(18).withMinute(0).withSecond(0);
                    this.respawnEnd = getDate().plusDays(6).withHour(23).withMinute(0).withSecond(0);
                    break;
                case "Antharas":
                    this.respawnStart = getDate().plusDays(10).withHour(18).withMinute(0).withSecond(0);
                    this.respawnEnd = getDate().plusDays(10).withHour(23).withMinute(0).withSecond(0);
                    break;
                case "Queen Ant":
                    this.respawnStart = getDate().plusDays(2).withHour(18).withMinute(0).withSecond(0);
                    this.respawnEnd = getDate().plusDays(2).withHour(23).withMinute(0).withSecond(0);
                    break;
                case "Valakas":
                    this.respawnStart = getDate().plusDays(10).withHour(18).withMinute(0).withSecond(0);
                    this.respawnEnd = getDate().plusDays(10).withHour(23).withMinute(0).withSecond(0);
                    break;
                case "Orfen":
                    this.respawnStart = getDate().plusDays(3).withHour(18).withMinute(0).withSecond(0);
                    this.respawnEnd = getDate().plusDays(3).withHour(23).withMinute(0).withSecond(0);
                    break;
                case "Baium":
                    this.respawnStart = getDate().plusDays(5).withHour(18).withMinute(0).withSecond(0);
                    this.respawnEnd = getDate().plusDays(5).withHour(23).withMinute(0).withSecond(0);
                    break;
            }
        }
    }
}
