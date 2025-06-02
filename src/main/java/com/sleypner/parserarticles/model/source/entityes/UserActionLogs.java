package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_action_logs")
public class UserActionLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private Users user;
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
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public UserActionLogs() {
        this.createdDate = LocalDateTime.now().withNano(0);
    }

    public UserActionLogs(String ip, String browser, String oc, String deviceType, String sessionId) {
        this.createdDate = LocalDateTime.now().withNano(0);
        this.ip = ip;
        this.browser = browser;
        this.oc = oc;
        this.deviceType = deviceType;
        this.sessionId = sessionId;
    }
}
