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
@Table(name = "events")
public class Events implements Comparable<Events> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "title")
    String title;
    @Column(name = "description",columnDefinition = "LONGTEXT")
    String description;
    @Column(name = "date",columnDefinition = "TIMESTAMP(0)")
    LocalDateTime date;
    @Column(name = "server")
    String server;
    @Column(name = "type")
    String type;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public Events(String title, String description, LocalDateTime date, String server,String type) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.server = server;
        this.type = type;
        this.createdDate = LocalDateTime.now().withNano(0);
    }

    @Override
    public int compareTo(Events o) {
        return getDate().compareTo(o.getDate());
    }
}
