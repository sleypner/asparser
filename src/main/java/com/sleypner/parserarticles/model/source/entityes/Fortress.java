package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fortress")
public class Fortress implements Comparable<Fortress> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "server")
    private String server;
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(
            name = "fortress_and_skills",
            joinColumns = @JoinColumn(name = "fortress_id"),
            inverseJoinColumns = @JoinColumn(name = "fortress_skills_id"))
    private Set<FortressSkills> skills = new HashSet<>();
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime updatedDate;

    public Fortress(String name, String server) {
        this.name = name;
        this.server = server;
        this.updatedDate = LocalDateTime.now().withNano(0);
    }


    public void setSkill(FortressSkills skills) {
        if(this.skills == null){
            this.skills = new HashSet<>();
        }
        this.skills.add(skills);
    }
    public void setSkillAll(Set<FortressSkills> skills) {
        if(this.skills == null){
            this.skills = new HashSet<>();
        }
        this.skills.addAll(skills);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Fortress)){
            return false;
        }
        Fortress fortressObj = (Fortress) obj;
        return Objects.equals(fortressObj.getName(), this.getName()) &&
                Objects.equals(fortressObj.getServer(), this.getServer());
    }

    @Override
    public int compareTo(Fortress o) {
        return Integer.compare(getId(),o.id);
    }
}

