package dev.sleypner.asparser.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "raid_bosses")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class RaidBoss extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @JoinColumn(name = "server_id")
    @ManyToOne()
    @ToString.Exclude
    private Server server;
    @Column(name = "date", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime date;
    @Column(name = "respawn_start", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime respawnStart;
    @Column(name = "respawn_end", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime respawnEnd;
    @Column(name = "count_killing")
    private int countKilling;
    @Column(name = "last_killer")
    private String lastKiller;
    @Column(name = "last_killers_clan")
    private String lastKillersClan;
    @Column(name = "attackers_count")
    private int attackersCount;

    public RaidBoss setDate(LocalDateTime date) {
        this.date = date;
        if (date != null) {
            SetRespawnBoss();
        }
        return this;
    }

    private void SetRespawnBoss() {
        String bossType = getType();
        String bossName = getName();
        if (Objects.equals(bossType, "Key Bosses")) {
            this.respawnStart = getDate().plusHours(18);
            this.respawnEnd = getDate().plusHours(30);
        } else if (Objects.equals(bossType, "Epic Bosses")) {
            switch (bossName) {
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

    @PrePersist
    private void onCreate() {
        super.setCreatedAt();
    }

    @PreUpdate
    private void onUpdate() {
        super.setUpdatedAt();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        RaidBoss that = (RaidBoss) o;
        return Objects.equals(getServer().getId(), that.getServer().getId()) && Objects.equals(getName(), that.getName());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
