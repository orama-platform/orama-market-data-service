package dev.kharraz.orama.marketdata.exchange;

import dev.kharraz.orama.marketdata.common.persistence.BaseEntity;
import dev.kharraz.orama.marketdata.country.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.NaturalId;

import java.util.Locale;

import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNull;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireValidText;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
    name = "exchange",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_exchange_country_name",
            columnNames = {"country_code", "name"}
        ),
        @UniqueConstraint(
            name = "uk_exchange_mic",
            columnNames = "mic"
        )
    }
)
@Getter
public class Exchange extends BaseEntity {

    private static final String EXCHANGE_MIC_REGEX = "^[A-Z0-9]{4}$";

    @Column(name = "name", nullable = false, updatable = false, length = 256)
    @NotBlank
    @Size(max = 256)
    private String name;

    @NaturalId
    @Column(name = "mic", nullable = false, updatable = false, length = 4)
    @Size(min = 4, max = 4)
    @Pattern(regexp = EXCHANGE_MIC_REGEX)
    private String mic;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "country_code",
        referencedColumnName = "iso_code_2",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_exchange_country")
    )
    private Country country;

    protected Exchange() {
    }

    public Exchange(String name, String mic, Country country) {
        this.name = requireValidName(name);
        this.mic = requireValidMic(mic);
        this.country = requireNonNull(country, "country");
    }

    private static String requireValidName(String name) {
        return requireValidText(name, 256, "exchange name");
    }

    private static String requireValidMic(String mic) {
        String normalizedMic = requireNonNull(mic, "exchange mic")
                .strip()
                .toUpperCase(Locale.ROOT);

        if (normalizedMic.isBlank()) {
            throw new IllegalArgumentException("exchange mic must not be blank");
        }
        if (normalizedMic.length() != 4) {
            throw new IllegalArgumentException("exchange mic must have exactly 4 characters");
        }
        if (!normalizedMic.matches(EXCHANGE_MIC_REGEX)) {
            throw new IllegalArgumentException("exchange mic must contain 4 uppercase letters or digits");
        }

        return normalizedMic;
    }

}
