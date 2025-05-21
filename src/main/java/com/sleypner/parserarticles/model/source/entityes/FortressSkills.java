package com.sleypner.parserarticles.model.source.entityes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fortress_skills")
public class FortressSkills {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "effect")
    private String effect;
    @Lob
    @Column(name = "image", columnDefinition="BLOB")
    private byte[] image;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "fortress_and_skills",
            joinColumns = @JoinColumn(name = "fortress_skills_id"),
            inverseJoinColumns = @JoinColumn(name = "fortress_id"))
    @JsonIgnore
    private Set<Fortress> fortress = new HashSet<>();
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public FortressSkills(String name, String effect, byte[] image, Set<Fortress> fortress) {
        this.createdDate = LocalDateTime.now().withNano(0);
        this.name = name;
        this.effect = effect;
        this.image = image;
        this.fortress = fortress;
    }
    public void setFort(Fortress fortress) {

        if (this.fortress == null){
            this.fortress = new HashSet<>();
        }
        this.fortress.add(fortress);
    }
    public void setFortAll(Set<Fortress> fortress) {

        if (this.fortress == null){
            this.fortress = new HashSet<>();
        }
        this.fortress.addAll(fortress);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof FortressSkills)){
            return false;
         }
        FortressSkills skillsObj = (FortressSkills) obj;

        return skillsObj.name.equals(this.name)
                && skillsObj.effect.equals(this.effect);
    }
}

