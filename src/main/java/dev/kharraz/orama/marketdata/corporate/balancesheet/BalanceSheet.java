package dev.kharraz.orama.marketdata.corporate.balancesheet;

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
    name = "balance_sheet",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_balance_sheet_corporate_report_date_period_type",
            columnNames = {"corporate_id", "report_date", "report_period_type"}
        )
    }
)
@Getter
public class BalanceSheet extends Auditable {

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
            name = "corporate_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_balance_sheet_corporate")
    )
    private Corporate corporate;

    @Column(name = "report_date", nullable = false, updatable = false)
    @NotNull
    private LocalDate reportDate;

    @Column(name = "report_period_type", nullable = false, updatable = false)
    @Enumerated(STRING)
    @NotNull
    private ReportPeriodType reportPeriodType;

    @Column(name = "accounts_receivable", precision = 19, scale = 4)
    private BigDecimal accountsReceivable;

    @Column(name = "current_assets", precision = 19, scale = 4)
    private BigDecimal currentAssets;

    @Column(name = "current_liabilities", precision = 19, scale = 4)
    private BigDecimal currentLiabilities;

    @Column(name = "inventory", precision = 19, scale = 4)
    private BigDecimal inventory;

    @Column(name = "non_current_assets", precision = 19, scale = 4)
    private BigDecimal nonCurrentAssets;

    @Column(name = "non_current_liabilities", precision = 19, scale = 4)
    private BigDecimal nonCurrentLiabilities;

    protected BalanceSheet() {
    }

    private BalanceSheet(Builder builder) {
        this.corporate = builder.corporate;
        this.reportDate = builder.reportDate;
        this.reportPeriodType = builder.reportPeriodType;

        this.accountsReceivable = builder.accountsReceivable;
        this.currentAssets = builder.currentAssets;
        this.currentLiabilities = builder.currentLiabilities;
        this.inventory = builder.inventory;
        this.nonCurrentAssets = builder.nonCurrentAssets;
        this.nonCurrentLiabilities = builder.nonCurrentLiabilities;
    }

    public static Builder builder(Corporate corporate, LocalDate reportDate, ReportPeriodType reportPeriodType) {
        return new Builder(corporate, reportDate, reportPeriodType);
    }

    public static final class Builder {
        private final Corporate corporate;
        private final LocalDate reportDate;
        private final ReportPeriodType reportPeriodType;

        private BigDecimal accountsReceivable;
        private BigDecimal currentAssets;
        private BigDecimal currentLiabilities;
        private BigDecimal inventory;
        private BigDecimal nonCurrentAssets;
        private BigDecimal nonCurrentLiabilities;

        private Builder(Corporate corporate, LocalDate reportDate, ReportPeriodType reportPeriodType) {
            this.corporate = requireNonNull(corporate, "corporate");
            this.reportDate = requireNonNull(reportDate, "report date");
            this.reportPeriodType = requireNonNull(reportPeriodType, "report period type");
        }

        public Builder accountsReceivable(BigDecimal accountsReceivable) {
            this.accountsReceivable = requireNonNegative(accountsReceivable, "accounts receivable");
            return this;
        }

        public Builder currentAssets(BigDecimal currentAssets) {
            this.currentAssets = requireNonNegative(currentAssets, "current assets");
            return this;
        }

        public Builder currentLiabilities(BigDecimal currentLiabilities) {
            this.currentLiabilities = requireNonNegative(currentLiabilities, "current liabilities");
            return this;
        }

        public Builder inventory(BigDecimal inventory) {
            this.inventory = requireNonNegative(inventory, "inventory");
            return this;
        }

        public Builder nonCurrentAssets(BigDecimal nonCurrentAssets) {
            this.nonCurrentAssets = requireNonNegative(nonCurrentAssets, "non-current assets");
            return this;
        }

        public Builder nonCurrentLiabilities(BigDecimal nonCurrentLiabilities) {
            this.nonCurrentLiabilities = requireNonNegative(nonCurrentLiabilities, "non-current liabilities");
            return this;
        }

        public BalanceSheet build() {
            return new BalanceSheet(this);
        }

    }

    public void updateAccountsReceivable(BigDecimal accountsReceivable) {
        this.accountsReceivable = requireNonNegative(accountsReceivable, "accounts receivable");
    }

    public void updateCurrentAssets(BigDecimal currentAssets) {
        this.currentAssets = requireNonNegative(currentAssets, "current assets");
    }

    public void updateCurrentLiabilities(BigDecimal currentLiabilities) {
        this.currentLiabilities = requireNonNegative(currentLiabilities, "current liabilities");
    }

    public void updateInventory(BigDecimal inventory) {
        this.inventory = requireNonNegative(inventory, "inventory");
    }

    public void updateNonCurrentAssets(BigDecimal nonCurrentAssets) {
        this.nonCurrentAssets = requireNonNegative(nonCurrentAssets, "non-current assets");
    }

    public void updateNonCurrentLiabilities(BigDecimal nonCurrentLiabilities) {
        this.nonCurrentLiabilities = requireNonNegative(nonCurrentLiabilities, "non-current liabilities");
    }

}