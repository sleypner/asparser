package dev.sleypner.asparser.domain.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.Objects;

@Entity
@Table(name = "user_action_logs")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class UserActionLog extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
    @Column(name = "ip")
    private String ip;
    @Column(name = "browser")
    private String browser;
    @Column(name = "oc")
    private String oc;
    @Column(name = "device_type")
    private String deviceType;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "action_type")
    private String actionType;

    public static UserActionLog getAction(User user, HttpServletRequest request, String actionType) {
        String userAgent = request.getHeader("User-Agent");

        Parser parser = new Parser();
        Client client = parser.parse(userAgent);

        return UserActionLog.builder()
                .ip(request.getRemoteAddr())
                .actionType(actionType)
                .oc(client.os.family)
                .browser(client.userAgent.family)
                .deviceType(userAgent)
                .sessionId(request.getSession().getId())
                .user(user)
                .build();
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
        UserActionLog that = (UserActionLog) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
