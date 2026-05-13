package dev.kharraz.orama.marketdata.country;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(
    name = "country",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_country_iso3", columnNames = "iso_code_3")
    }
)
@Getter
public class Country {

    @Id
    @NaturalId
    @Column(name = "iso_code_2", nullable = false, updatable = false, length = 2)
    @Size(min = 2, max = 2)
    @Pattern(regexp = "^[A-Z]{2}$")
    private String isoCode2;

    @Column(name = "iso_code_3", nullable = false, updatable = false, length = 3)
    @Size(min = 3, max = 3)
    @Pattern(regexp = "^[A-Z]{3}$")
    private String isoCode3;

    @Column(name = "name", nullable = false, updatable = false, length = 128)
    @NotBlank
    @Size(max = 128)
    private String name;

    protected Country() {
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Country country = (Country) o;
        return isoCode2 != null && isoCode2.equals(country.isoCode2);
    }

    @Override
    public final int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

}
