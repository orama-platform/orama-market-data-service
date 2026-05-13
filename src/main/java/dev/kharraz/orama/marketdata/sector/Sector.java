package dev.kharraz.orama.marketdata.sector;

import dev.kharraz.orama.marketdata.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireValidText;

@Entity
@Table(
    name = "sector",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_sector_name",
        columnNames = "name"
    )
)
@Getter
public class Sector extends BaseEntity {

    @Column(name = "name", nullable = false, updatable = false, length = 128)
    @NotBlank
    @Size(max = 128)
    private String name;

    protected Sector() {
    }

    public Sector(String name) {
        this.name = requireValidName(name);
    }

    private static String requireValidName(String name) {
        return requireValidText(name, 128, "sector name");
    }

}
