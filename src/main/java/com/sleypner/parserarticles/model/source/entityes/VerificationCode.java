package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@Table
@Entity
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private int verificationCode;
    @Column
    private LocalDateTime expiryDate;
    @Column
    private LocalDateTime verifiedAt;
    @Column
    private String email;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public VerificationCode(String email, int verificationCode) {
        this.verificationCode = verificationCode;
        this.createdDate = LocalDateTime.now().withNano(0);
        this.expiryDate = LocalDateTime.now().plusMinutes(1);
        this.email = email;
    }

    public boolean isExpired(){
        return LocalDateTime.now().isBefore(expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VerificationCode that)) return false;
        return verificationCode == that.verificationCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, verificationCode, createdDate, expiryDate, verifiedAt, email);
    }
}
