package dev.kharraz.orama.marketdata.currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.Hibernate;

@Entity
@Table(name = "currency")
@Getter
public class Currency {

    @Id
    @Column(name = "code", nullable = false, updatable = false, length = 3)
    @Size(min = 3, max = 3)
    @Pattern(regexp = "^[A-Z]{3}$")
    private String code;

    @Column(name = "name", nullable = false, updatable = false, length = 64)
    @Size(max = 64)
    @NotBlank
    private String name;

    @Column(name = "symbol", updatable = false, length = 8)
    @Size(max = 8)
    private String symbol;

    protected Currency() {
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Currency currency = (Currency) o;
        return code != null && code.equals(currency.code);
    }

    @Override
    public final int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

}
