package dev.kharraz.orama.marketdata.corporate.incomestatement;

import dev.kharraz.orama.marketdata.common.persistence.Auditable;
import dev.kharraz.orama.marketdata.common.type.ReportPeriodType;
import dev.kharraz.orama.marketdata.corporate.Corporate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
    name = "income_statement",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_income_statement_corporate_report_date_period_type",
        columnNames = {"corporate_id", "report_date", "report_period_type"}
    )
)
@Getter
public class IncomeStatement extends Auditable {

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "corporate_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_income_statement_corporate")
    )
    private Corporate corporate;

    @Column(name = "report_date", nullable = false, updatable = false)
    @NotNull
    private LocalDate reportDate;

    @Column(name = "report_period_type", nullable = false, updatable = false)
    @Enumerated(STRING)
    @NotNull
    private ReportPeriodType reportPeriodType;

    @Column(name = "revenue", precision = 19, scale = 4)
    private BigDecimal revenue;

    @Column(name = "ebit", precision = 19, scale = 4)
    private BigDecimal ebit;

    @Column(name = "ebitda", precision = 19, scale = 4)
    private BigDecimal ebitda;

    @Column(name = "net_income", precision = 19, scale = 4)
    private BigDecimal netIncome;

    @Column(name = "operating_expenses", precision = 19, scale = 4)
    private BigDecimal operatingExpenses;

    protected IncomeStatement() {
    }

    private IncomeStatement(Builder builder) {
        this.corporate = builder.corporate;
        this.reportDate = builder.reportDate;
        this.reportPeriodType = builder.reportPeriodType;
        this.revenue = builder.revenue;
        this.ebit = builder.ebit;
        this.ebitda = builder.ebitda;
        this.netIncome = builder.netIncome;
        this.operatingExpenses = builder.operatingExpenses;
    }

    public static Builder builder(Corporate corporate, LocalDate reportDate, ReportPeriodType reportPeriodType) {
        return new Builder(corporate, reportDate, reportPeriodType);
    }

    public static final class Builder {
        private final Corporate corporate;
        private final LocalDate reportDate;
        private final ReportPeriodType reportPeriodType;

        private BigDecimal revenue;
        private BigDecimal ebit;
        private BigDecimal ebitda;
        private BigDecimal netIncome;
        private BigDecimal operatingExpenses;

        private Builder(Corporate corporate, LocalDate reportDate, ReportPeriodType reportPeriodType) {
            this.corporate = requireNonNull(corporate, "corporate");
            this.reportDate = requireNonNull(reportDate, "report date");
            this.reportPeriodType = requireNonNull(reportPeriodType, "report period type");
        }

        public Builder revenue(BigDecimal revenue) {
            this.revenue = requireNonNegative(revenue, "revenue");
            return this;
        }

        public Builder ebit(BigDecimal ebit) {
            this.ebit = requireNonNull(ebit, "ebit");
            return this;
        }

        public Builder ebitda(BigDecimal ebitda) {
            this.ebitda = requireNonNull(ebitda, "ebitda");
            return this;
        }

        public Builder netIncome(BigDecimal netIncome) {
            this.netIncome = requireNonNull(netIncome, "net income");
            return this;
        }

        public Builder operatingExpenses(BigDecimal operatingExpenses) {
            this.operatingExpenses = requireNonNegative(operatingExpenses, "operating expenses");
            return this;
        }

        public IncomeStatement build() {
            return new IncomeStatement(this);
        }

    }

    public void updateRevenue(BigDecimal revenue) {
        this.revenue = requireNonNegative(revenue, "revenue");
    }

    public void updateEbit(BigDecimal ebit) {
        this.ebit = requireNonNull(ebit, "ebit");
    }

    public void updateEbitda(BigDecimal ebitda) {
        this.ebitda = requireNonNull(ebitda, "ebitda");
    }

    public void updateNetIncome(BigDecimal netIncome) {
        this.netIncome = requireNonNull(netIncome, "net income");
    }

    public void updateOperatingExpenses(BigDecimal operatingExpenses) {
        this.operatingExpenses = requireNonNegative(operatingExpenses, "operating expenses");
    }

}
