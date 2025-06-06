package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public class AuditableEntity {

    @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime createdDate;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime updatedDate;

    protected void setCreatedAt() {
        createdDate = LocalDateTime.now().withNano(0);
        updatedDate = LocalDateTime.now().withNano(0);
    }

    protected void setUpdatedAt() {
        updatedDate = LocalDateTime.now().withNano(0);
    }
}
