package dev.kharraz.orama.marketdata.corporate.quote;

import dev.kharraz.orama.marketdata.common.persistence.BaseEntity;
import dev.kharraz.orama.marketdata.corporate.Corporate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNegative;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNull;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requirePositive;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
    name = "quote",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_quote_corporate_as_of",
        columnNames = {"corporate_id", "as_of_ts"}
    )
)
@Getter
public class Quote extends BaseEntity {

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "corporate_id",
        nullable = false,
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_quote_corporate")
    )
    private Corporate corporate;

    @NotNull
    @Column(name = "as_of_ts", nullable = false, updatable = false)
    private OffsetDateTime asOfTs;

    @NotNull
    @Column(name = "price", nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @NotNull
    @Column(name = "day_high", nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal dayHigh;

    @NotNull
    @Column(name = "day_low", nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal dayLow;

    @NotNull
    @Column(name = "year_high", nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal yearHigh;

    @NotNull
    @Column(name = "year_low", nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal yearLow;

    @NotNull
    @Column(name = "shares_outstanding", nullable = false, updatable = false)
    private Long sharesOutstanding;

    protected Quote() {
    }

    public Quote(
        Corporate corporate,
        OffsetDateTime asOfTs,
        BigDecimal price,
        BigDecimal dayHigh,
        BigDecimal dayLow,
        BigDecimal yearHigh,
        BigDecimal yearLow,
        Long sharesOutstanding
    ) {
        this.corporate = requireNonNull(corporate, "corporate");
        this.asOfTs = requireNonNull(asOfTs, "as of timestamp");
        this.price = requireNonNegative(price, "quote price");
        this.dayHigh = requireNonNegative(dayHigh, "day high");
        this.dayLow = requireNonNegative(dayLow, "day low");
        this.yearHigh = requireNonNegative(yearHigh, "year high");
        this.yearLow = requireNonNegative(yearLow, "year low");
        this.sharesOutstanding = requirePositive(sharesOutstanding, "shares outstanding");

        validatePriceRanges();
    }

    private void validatePriceRanges() {
        if (dayLow.compareTo(dayHigh) > 0)
            throw new IllegalArgumentException("day high must be greater than or equal to day low");
        if (dayLow.compareTo(price) > 0)
            throw new IllegalArgumentException("price must be greater than or equal to day low");
        if (yearLow.compareTo(yearHigh) > 0)
            throw new IllegalArgumentException("year high must be greater than or equal to year low");
        if (yearLow.compareTo(price) > 0)
            throw new IllegalArgumentException("price must be greater than or equal to year low");
        if (price.compareTo(dayHigh) > 0)
            throw new IllegalArgumentException("price must be less than or equal to day high");
        if (price.compareTo(yearHigh) > 0)
            throw new IllegalArgumentException("price must be less than or equal to year high");
        if (dayHigh.compareTo(yearHigh) > 0)
            throw new IllegalArgumentException("day high must be less than or equal to year high");
        if (dayLow.compareTo(yearLow) < 0)
            throw new IllegalArgumentException("day low must be greater than or equal to year low");
    }

}
