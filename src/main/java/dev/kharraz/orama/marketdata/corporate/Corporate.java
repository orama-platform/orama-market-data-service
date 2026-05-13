package dev.kharraz.orama.marketdata.corporate;

import dev.kharraz.orama.marketdata.common.persistence.Auditable;
import dev.kharraz.orama.marketdata.country.Country;
import dev.kharraz.orama.marketdata.currency.Currency;
import dev.kharraz.orama.marketdata.exchange.Exchange;
import dev.kharraz.orama.marketdata.industry.Industry;
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

import java.util.Locale;

import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNull;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireValidText;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
    name = "corporate",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_corporate_exchange_ticker",
            columnNames = {"exchange_id", "ticker"}
        ),
        @UniqueConstraint(
            name = "uk_corporate_isin",
            columnNames = "isin"
        )
    }
)
@Getter
public class Corporate extends Auditable {

    private static final String CORPORATE_ISIN_REGEX = "^[A-Z]{2}[A-Z0-9]{9}[0-9]$";

    @Column(name = "ticker", nullable = false, updatable = false, length = 16)
    @NotBlank
    @Size(max = 16)
    private String ticker;

    @Column(name = "isin", length = 12)
    @Size(min = 12, max = 12)
    @Pattern(regexp = CORPORATE_ISIN_REGEX)
    private String isin;

    @Column(name = "name", nullable = false, length = 256)
    @NotBlank
    @Size(max = 256)
    private String name;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "country_code",
        referencedColumnName = "iso_code_2",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_corporate_country")
    )
    private Country country;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "currency_code",
        referencedColumnName = "code",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_corporate_currency")
    )
    private Currency currency;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "exchange_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_corporate_exchange")
    )
    private Exchange exchange;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
        name = "industry_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_corporate_industry")
    )
    private Industry industry;

    protected Corporate() {
    }

    public Corporate(
        String ticker,
        String name,
        Country country,
        Currency currency,
        Exchange exchange,
        Industry industry
    ) {
        this.ticker = requireValidTicker(ticker);
        this.name = requireValidName(name);
        this.country = requireNonNull(country, "country");
        this.currency = requireNonNull(currency, "currency");
        this.exchange = requireNonNull(exchange, "exchange");
        this.industry = requireNonNull(industry, "industry");
    }

    private static String requireValidTicker(String ticker) {
        return requireValidText(ticker, 16, "corporate ticker")
                .toUpperCase(Locale.ROOT);
    }

    private static String requireValidName(String name) {
        return requireValidText(name, 256, "corporate name");
    }

    public void rename(String name) {
        this.name = requireValidName(name);
    }

    public void updateIsin(String isin) {
        this.isin = normalizeIsin(isin);
    }

    private static String normalizeIsin(String isin) {
        String normalizedIsin = requireNonNull(isin, "corporate isin")
                .strip()
                .toUpperCase(Locale.ROOT);

        if (normalizedIsin.isBlank())
            throw new IllegalArgumentException("corporate isin must not be blank");

        if (normalizedIsin.length() != 12)
            throw new IllegalArgumentException("corporate isin must have exactly 12 characters");

        if (!normalizedIsin.matches(CORPORATE_ISIN_REGEX))
            throw new IllegalArgumentException("corporate isin must match the ISO 6166 format");

        return normalizedIsin;
    }

    public void reclassifyIndustry(Industry industry) {
        this.industry = requireNonNull(industry, "industry");
    }

}
