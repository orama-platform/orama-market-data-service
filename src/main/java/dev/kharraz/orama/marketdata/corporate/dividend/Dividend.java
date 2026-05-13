package dev.kharraz.orama.marketdata.corporate.dividend;

import dev.kharraz.orama.marketdata.common.persistence.Auditable;
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
import java.time.LocalDate;

import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNegative;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNull;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireOnOrAfter;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
    name = "dividend",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_dividend_corporate_ex_date",
            columnNames = {"corporate_id", "ex_date"}
        )
    }
)
@Getter
public class Dividend extends Auditable {

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "corporate_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_dividend_corporate")
    )
    private Corporate corporate;

    @Column(name = "ex_date", nullable = false, updatable = false)
    @NotNull
    private LocalDate exDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "rate", nullable = false, updatable = false, precision = 19, scale = 4)
    @NotNull
    private BigDecimal rate;

    @Column(name = "five_year_avg", precision = 19, scale = 4)
    private BigDecimal fiveYearAvg;

    @Column(name = "forward_rate", precision = 19, scale = 4)
    private BigDecimal forwardRate;

    @Column(name = "payout_ratio", precision = 19, scale = 2)
    private BigDecimal payoutRatio;

    protected Dividend() {
    }

    private Dividend(Builder builder) {
        this.corporate = builder.corporate;
        this.exDate = builder.exDate;
        this.paymentDate = builder.paymentDate;
        this.rate = builder.rate;
        this.fiveYearAvg = builder.fiveYearAvg;
        this.forwardRate = builder.forwardRate;
        this.payoutRatio = builder.payoutRatio;
    }

    public static Builder builder(Corporate corporate, LocalDate exDate, BigDecimal rate) {
        return new Builder(corporate, exDate, rate);
    }

    public static final class Builder {
        private final Corporate corporate;
        private final LocalDate exDate;
        private final BigDecimal rate;

        private LocalDate paymentDate;
        private BigDecimal fiveYearAvg;
        private BigDecimal forwardRate;
        private BigDecimal payoutRatio;

        private Builder(Corporate corporate, LocalDate exDate, BigDecimal rate) {
            this.corporate = requireNonNull(corporate, "corporate");
            this.exDate = requireNonNull(exDate, "ex date");
            this.rate = requireNonNegative(rate, "rate");
        }

        public Builder paymentDate(LocalDate paymentDate) {
            this.paymentDate = requireOnOrAfter(paymentDate, "payment date", exDate, "ex date");
            return this;
        }

        public Builder fiveYearAvg(BigDecimal fiveYearAvg) {
            this.fiveYearAvg = requireNonNegative(fiveYearAvg, "five year average");
            return this;
        }

        public Builder forwardRate(BigDecimal forwardRate) {
            this.forwardRate = requireNonNegative(forwardRate, "forward rate");
            return this;
        }

        public Builder payoutRatio(BigDecimal payoutRatio) {
            this.payoutRatio = requireNonNull(payoutRatio, "payout ratio");
            return this;
        }

        public Dividend build() {
            return new Dividend(this);
        }

    }

    public void updatePaymentDate(LocalDate paymentDate) {
        this.paymentDate = requireOnOrAfter(paymentDate, "payment date", exDate, "ex date");
    }

    public void updateFiveYearAvg(BigDecimal fiveYearAvg) {
        this.fiveYearAvg = requireNonNegative(fiveYearAvg, "five year average");
    }

    public void updateForwardRate(BigDecimal forwardRate) {
        this.forwardRate = requireNonNegative(forwardRate, "forward rate");
    }

    public void updatePayoutRatio(BigDecimal payoutRatio) {
        this.payoutRatio = requireNonNull(payoutRatio, "payout ratio");
    }

}
