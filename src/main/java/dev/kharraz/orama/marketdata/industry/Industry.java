package dev.kharraz.orama.marketdata.industry;

import dev.kharraz.orama.marketdata.common.persistence.BaseEntity;
import dev.kharraz.orama.marketdata.sector.Sector;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNull;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireValidText;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
    name = "industry",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_industry_sector_name",
        columnNames = {"sector_id", "name"}
    )
)
@Getter
public class Industry extends BaseEntity {

    @Column(name = "name", nullable = false, updatable = false, length = 128)
    @NotBlank
    @Size(max = 128)
    private String name;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "sector_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_industry_sector")
    )
    private Sector sector;

    protected Industry() {
    }

    public Industry(String name, Sector sector) {
        this.name = requireValidName(name);
        this.sector = requireNonNull(sector, "sector");
    }

    private static String requireValidName(String name) {
        return requireValidText(name, 128, "industry name");
    }

}
