package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "online_status_new")
public class OnlineStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;
    @Column(name = "server_name")
    public String serverName;
    @Column(name = "online")
    public short online;
    @Column(name = "on_trade")
    public short onTrade;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public OnlineStatus(LocalDateTime createDate, String serverName, short online, short onTrade) {
        this.createdDate = createDate;
        this.serverName = serverName;
        this.online = online;
        this.onTrade = onTrade;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OnlineStatus.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("createdDate=" + createdDate)
                .add("serverName='" + serverName + "'")
                .add("online=" + online)
                .add("onTrade=" + onTrade)
                .toString();
    }
}
