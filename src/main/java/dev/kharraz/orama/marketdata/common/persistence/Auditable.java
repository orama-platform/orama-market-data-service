package dev.kharraz.orama.marketdata.common.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@MappedSuperclass
public abstract class Auditable extends BaseEntity {

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    protected Auditable() {
    }

}
